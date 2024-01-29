package com.drcome.project.common.mapper;

import java.util.List;

import com.drcome.project.common.service.UserMemberVO;

public interface UserMemberMapper {

	List<UserMemberVO> selectMemberList(UserMemberVO vo);

	int insertUserMember(UserMemberVO vo);
	
	UserMemberVO selectUser(UserMemberVO vo);
}
