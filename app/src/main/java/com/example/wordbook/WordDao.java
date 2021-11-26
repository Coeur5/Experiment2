package com.example.wordbook;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WordDao {
    @Insert
    void insertWords(Word... words);
    @Update
    void updateWords(Word... words);
    @Delete
    void deleteWords(Word... words);

    @Query("delete from word")
    void deleteAllWords();

    @Query("select * from word order by id desc")
    LiveData<List<Word>> getAllWordsLive();

    @Query("select * from word where english_word like :patten order by id desc ")
    LiveData<List<Word>> findWordsWithPattern(String patten);



}
