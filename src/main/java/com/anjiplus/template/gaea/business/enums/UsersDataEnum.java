package com.anjiplus.template.gaea.business.enums;

public enum UsersDataEnum {
    USERS_DATA_ENUM_ONE(1,"学生数据"),
    USERS_DATA_ENUM_TWO(2, "生活数据"),
    USERS_DATA_ENUM_THEERE(3, "导师数据");


    private int key;

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    UsersDataEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }
    UsersDataEnum() {
    }

    public static String getValue(Integer key) {
        if (key == null) {
            return null;
        }
        UsersDataEnum[] enums = values();
        for (UsersDataEnum enu : enums) {
            if (enu.getKey() == key) {
                return enu.getValue();
            }
        }
        return null;
    }
}
