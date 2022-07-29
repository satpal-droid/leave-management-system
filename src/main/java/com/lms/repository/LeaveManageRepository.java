package com.lms.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.lms.models.LeaveDetails;

@Repository(value = "leaveManageRepository")
public interface LeaveManageRepository extends JpaRepository<LeaveDetails, Serializable> {



    @Query(value = "select total_leaves from total_leaves", nativeQuery = true)
    Integer totalLeaves();
    
    @Query(value = "select sum(duration) from leave_details where username=? AND accept_reject_flag=true", nativeQuery = true)
    Integer countActiveLeaves(String username); 
    
    @Query(value = "select sum(duration) from leave_details where username=? AND accept_reject_flag=false", nativeQuery = true)
    Integer  countRejectedLeaves(String username);  
    
    @Query(nativeQuery = true, value = "select array_to_json(array_agg(row_to_json(t))) from (select employee_name||' on leave' as title,to_char(from_date,'YYYY-MM-DD') as start,to_char(to_date,'YYYY-MM-DD') as end from leave_details) as t;")
    public Object getAllLeavesAsJsonArray();
    
    // @Query(nativeQuery = true, value = "select sum (duration) from leave_details where accept_reject_flag = false")
    // public List<LeaveDetails> getAllRejectedLeavesById();
   
    @Transactional
    @Query(value="select * from leave_details where active = true", nativeQuery = true)
    public List<LeaveDetails> countAllActiveleaves();

    @Query(nativeQuery = true, value = "select * from leave_details where username=? order by id desc")
    public List<LeaveDetails> getAllLeavesOfUser(String username);


}
