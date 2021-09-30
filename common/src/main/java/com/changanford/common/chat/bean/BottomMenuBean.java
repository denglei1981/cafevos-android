package com.changanford.common.chat.bean;

/**
 * 文件名：BottomMenuBean
 * 创建者: zcy
 * 创建日期：2020/10/14 10:37
 * 描述: TODO
 * 修改描述：TODO
 */
public class BottomMenuBean {

    public BottomMenuBean(String menuName, int menuIcon) {
        this.menuName = menuName;
        this.menuIcon = menuIcon;
    }

    private int menuIcon;
    private String menuName;
    private String menuId;

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public int getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(int menuIcon) {
        this.menuIcon = menuIcon;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }
}
