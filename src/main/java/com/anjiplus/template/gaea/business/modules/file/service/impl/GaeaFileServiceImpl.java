package com.anjiplus.template.gaea.business.modules.file.service.impl;

import com.anji.plus.gaea.constant.BaseOperationEnum;
import com.anji.plus.gaea.curd.mapper.GaeaBaseMapper;
import com.anji.plus.gaea.exception.BusinessException;
import com.anji.plus.gaea.exception.BusinessExceptionBuilder;
import com.anjiplus.template.gaea.business.code.ResponseCode;
import com.anjiplus.template.gaea.business.modules.file.dao.GaeaFileMapper;
import com.anjiplus.template.gaea.business.modules.file.entity.GaeaFile;
import com.anjiplus.template.gaea.business.modules.file.service.GaeaFileService;
import com.anjiplus.template.gaea.business.modules.file.util.FileUtils;
import com.anjiplus.template.gaea.business.modules.file.util.StringPatternUtil;
import com.anjiplus.template.gaea.business.util.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * (GaeaFile)ServiceImpl
 *
 * @author peiyanni
 * @since 2021-02-18 14:48:26
 */
@Service
@Slf4j
public class GaeaFileServiceImpl implements GaeaFileService {

    @Value("${customer.file.dist-path:''}")
    private String dictPath;

    @Value("${customer.file.white-list:''}")
    private String whiteList;

    @Value("${customer.file.excelSuffix:''}")
    private String excelSuffix;

    @Value("${customer.file.downloadPath:''}")
    private String fileDownloadPath;

    @Autowired
    private GaeaFileMapper gaeaFileMapper;

    @Override
    public GaeaBaseMapper<GaeaFile> getMapper() {
        return gaeaFileMapper;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public GaeaFile upload(MultipartFile multipartFile, File file, String customFileName) {
        try {
            String fileName = "";
            if (null != multipartFile) {
                fileName = multipartFile.getOriginalFilename();
            }else {
                fileName = file.getName();
            }

            if (StringUtils.isBlank(fileName)) {
                throw BusinessExceptionBuilder.build(ResponseCode.FILE_EMPTY_FILENAME);
            }

            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            String fileInstruction = fileName.substring(0, fileName.lastIndexOf("."));
            //???????????????(??????????????????)
            List<String> list = new ArrayList<>(Arrays.asList(whiteList.split("\\|")));
            list.addAll(list.stream().map(String::toUpperCase).collect(Collectors.toList()));
            if (!list.contains(suffixName)) {
                throw BusinessExceptionBuilder.build(ResponseCode.FILE_SUFFIX_UNSUPPORTED);
            }
            // ???????????????????????????
            String fileId;
            if (StringUtils.isBlank(customFileName)) {
                fileId = UUID.randomUUID().toString();
            } else {
                fileId = customFileName;
            }
            String newFileName = fileId + suffixName;
            // ????????????????????????
            String filePath = dictPath + newFileName;
            String urlPath = fileDownloadPath+ "/"  + newFileName;
            GaeaFile gaeaFile = new GaeaFile();
            gaeaFile.setFilePath(filePath);
            gaeaFile.setFileId(fileId);
            gaeaFile.setUrlPath(urlPath);
            gaeaFile.setFileType(suffixName.replace(".", ""));
            gaeaFile.setFileInstruction(fileInstruction);
            gaeaFileMapper.insert(gaeaFile);

            //????????? ???????????????/app/dictPath/upload/???
            java.io.File dest = new java.io.File(dictPath + newFileName);
            java.io.File parentFile = dest.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            if (null != multipartFile) {
                multipartFile.transferTo(dest);
            }else {
                FileUtil.copyFileUsingFileChannels(file, dest);
            }
            // ????????????http??????????????????
            return gaeaFile;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("file upload error: {}", e);
            throw BusinessExceptionBuilder.build(ResponseCode.FILE_UPLOAD_ERROR);
        }
    }

    /**
     * ????????????
     *
     * @param multipartFile ??????
     * @return
     */
    @Override
    public GaeaFile upload(MultipartFile multipartFile) {
        return upload(multipartFile, null, null);
    }

    /**
     * ????????????
     *
     * @param file           ??????
     * @param customFileName ??????????????????
     * @return
     */
    @Override
    public GaeaFile upload(File file, String customFileName) {
        return upload(null, file, customFileName);
    }

    @Override
    public ResponseEntity<byte[]> download(HttpServletRequest request, HttpServletResponse response, String fileId) {
        try {
            String userAgent = request.getHeader("User-Agent");
            boolean isIeBrowser = userAgent.indexOf("MSIE") > 0;
            //??????fileId??????gaea_file?????????filePath
            LambdaQueryWrapper<GaeaFile> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(GaeaFile::getFileId, fileId);
            GaeaFile gaeaFile = gaeaFileMapper.selectOne(queryWrapper);
            if (null == gaeaFile) {
                throw BusinessExceptionBuilder.build(ResponseCode.FILE_ONT_EXSIT);
            }
            //???????????????????????????????????????
            String filePath = gaeaFile.getFilePath();
            if (StringUtils.isBlank(filePath)) {
                throw BusinessExceptionBuilder.build(ResponseCode.FILE_ONT_EXSIT);
            }
            String filename = filePath.substring(filePath.lastIndexOf(File.separator));
            String fileSuffix = filename.substring(filename.lastIndexOf("."));

            //?????????????????????????????????????????????\??????\???????????????????????????
            File file = new File(filePath);
            ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
            builder.contentLength(file.length());
            if (StringPatternUtil.stringMatchIgnoreCase(fileSuffix, "(.png|.jpg|.jpeg|.bmp|.gif|.icon)")) {
                builder.cacheControl(CacheControl.noCache()).contentType(MediaType.IMAGE_PNG);
            } else if (StringPatternUtil.stringMatchIgnoreCase(fileSuffix, "(.flv|.swf|.mkv|.avi|.rm|.rmvb|.mpeg|.mpg|.ogg|.ogv|.mov|.wmv|.mp4|.webm|.wav|.mid|.mp3|.aac)")) {
                builder.header("Content-Type", "video/mp4; charset=UTF-8");
            } else {
                //application/octet-stream ????????????????????????????????????????????????
                builder.contentType(MediaType.APPLICATION_OCTET_STREAM);
                filename = URLEncoder.encode(filename, "UTF-8");
                if (isIeBrowser) {
                    builder.header("Content-Disposition", "attachment; filename=" + filename);
                } else {
                    builder.header("Content-Disposition", "attacher; filename*=UTF-8''" + filename);
                }
            }
            return builder.body(FileUtils.readFileToByteArray(file));
        } catch (Exception e) {
            log.error("file download error: {}", e);
            return null;
        }
    }

    /**
     * ???????????????????????????
     * ?????????????????????????????????
     *
     * @param entities
     * @param operationEnum ????????????
     * @throws BusinessException ???????????????????????????????????????
     */
    @Override
    public void processBatchAfterOperation(List<GaeaFile> entities, BaseOperationEnum operationEnum) throws BusinessException {
        if (operationEnum.equals(BaseOperationEnum.DELETE_BATCH)) {
            // ??????????????????
            entities.forEach(gaeaFile -> {
                String filePath = gaeaFile.getFilePath();
                File file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                }
            });
        }

    }
}
