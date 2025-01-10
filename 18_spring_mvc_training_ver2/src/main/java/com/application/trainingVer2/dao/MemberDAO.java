package com.application.trainingVer2.dao;

import org.apache.ibatis.annotations.Mapper;

import com.application.trainingVer2.dto.MemberDTO;

@Mapper
public interface MemberDAO {

	public void createMember(MemberDTO memberDTO);  			// 회원가입 DAO
	public String checkValidId(String memberId); 				// (회원가입) 아이디 중복체크 DAO 
	public String getEncodedPasswd(String memberId);			// 암호화된 패스워드 조회 DAO
	public MemberDTO getMemberDetail(String memberId);			// 회원정보 상세조회 DAO
	public void updateMember(MemberDTO memberDTO);				// 회원정보 수정 DAO
	public String getDeleteMemberProfileUuid(String memberId);	// 탈퇴회원 프로필 조회
	public void deleteMember(String memberId);					// 회원탈퇴 DAO
	
}
