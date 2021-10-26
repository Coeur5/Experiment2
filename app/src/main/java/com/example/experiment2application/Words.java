package com.example.experiment2application;

import android.provider.BaseColumns;

public class Words {
    public Words(){}
    public static abstract class Word implements BaseColumns{
        public static final String TABLE_NAME="words";
        public static final String COLUMN_WORD="word";//单词列
        public static final String COLUMN_SAMPLE="sample";//单词示例
        public static final String COLUMN_MEANING="meaning";//单词含义列
    }
}
