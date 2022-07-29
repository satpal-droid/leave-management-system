package com.lms.repository;

// import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lms.models.UserInfo;

@Repository(value = "userInfoRepository")
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

    public UserInfo findByEmail(String email);

    
    @Query(value = "SELECT * FROM userinfo ", nativeQuery = true)
    List <UserInfo> findAll(); 

   
    public UserInfo findById(int id);

    @Transactional
    @Modifying
    @Query(value = "update userinfo set active=false where id=?", nativeQuery = true)
    public void blockUser(int id);

    @Transactional
    @Modifying
    @Query(value = "update userinfo set active=true where id=?", nativeQuery = true)
    public void unBlockUser(int id);


    public List<UserInfo> findAllByOrderById();
}
