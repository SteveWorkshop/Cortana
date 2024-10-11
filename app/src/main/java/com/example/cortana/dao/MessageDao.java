package com.example.cortana.dao;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.cortana.entity.Message;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert
    Long insertMessage(Message message);

    @Update
    int updateMessage(Message message);

    @Query("select * from Message order by updateTime")
    List<Message> getAll();//普通查询

    @Query("select * from Message order by updateTime")
    DataSource.Factory<Integer,Message> getAll_v2();

    @Query("update Message set isDeleted=1 where id=:id")
    int deleteById(Integer id);

    @Query("update Message set isDeleted=0 where id=:id")
    int recycleById(Integer id);

    @Query("delete from Message where id=:id")
    int eraseById(Integer id);
}
