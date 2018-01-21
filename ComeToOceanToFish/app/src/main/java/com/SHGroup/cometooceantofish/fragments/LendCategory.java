package com.SHGroup.cometooceantofish.fragments;

public enum LendCategory {
    FISHING_ROD("낚싯대", 0), BAIT("미끼", 1), FOOD("식료품", 2), SHEEP("선박", 3), ETC("기타", 0x50);
    private final String kor;
    private final int code;

    private LendCategory(String kor, int code){
        this.kor = kor;
        this.code = code;
    }

    public final String getKorean(){
        return kor;
    }

    public final int getCode(){
        return code;
    }

    public static LendCategory getCategoryFromCode(int code){
        for(LendCategory lc : values()){
            if(lc.getCode() == code){
                return lc;
            }
        }
        return null;
    }

    public static LendCategory getCategoryFromKorean(String kor){
        for(LendCategory lc : values()){
            if(lc.getKorean().equals(kor)){
                return lc;
            }
        }
        return null;
    }
}
