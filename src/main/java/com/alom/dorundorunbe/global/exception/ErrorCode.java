package com.alom.dorundorunbe.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  // 챌린지
  NO_SUCH_CHALLENGE(HttpStatus.NOT_FOUND, "존재하지 않는 챌린지입니다."),
  // 업적
  ACHIEVEMENT_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 업적입니다."),
  ACHIEVEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "업적을 찾을 수 없습니다."),
  ACHIEVEMENT_CONDITION_NOT_MET(HttpStatus.BAD_REQUEST, "해당 업적 조건이 충족되지 않았습니다."),
  USER_ACHIEVEMENT_ALREADY_CLAIMED(HttpStatus.CONFLICT, "이미 해당 업적을 받았습니다."),
  USER_ACHIEVEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자 업적을 찾을 수 없습니다."),
  REWARD_ALREADY_CLAIMED(HttpStatus.CONFLICT, "이미 보상을 수령한 업적입니다."),

  //랭킹
  RANKING_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 랭킹을 찾을 수 없습니다."),
  RANKING_TIER_MISMATCH(HttpStatus.BAD_REQUEST, "사용자의 티어가 랭킹과 일치하지 않습니다."),
  RANKING_ALREADY_PARTICIPATED(HttpStatus.CONFLICT, "사용자가 이미 해당 랭킹에 참가 중입니다."),
  RANKING_MINIMUM_RECORDS_NOT_MET(HttpStatus.BAD_REQUEST, "랭킹 참가 이후 5km 기록이 3회 필요합니다."),
  USER_ALREADY_IN_RANKING(HttpStatus.CONFLICT, "사용자가 이미 랭킹에 참여 중입니다."),
  USER_RANKING_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자의 랭킹 기록을 찾을 수 없습니다."),

  //Doodle 오류
  DOODLE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 방입니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),

  //UserDoodle
    USER_DOODLE_NOT_FOUND(HttpStatus.NOT_FOUND, "유저의 두들 기록을 찾을 수 없습니다."),

  // 알려지지 않은 문제
  UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR, "알려지지 않은 문제가 발생하였습니다."),
  // 입력 값 오류
  BLANK_ARGUMENT(HttpStatus.BAD_REQUEST, "비어있는 값이 존재합니다."),
  INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "이메일 형식이 올바르지 않습니다."),
  PASSWORD_POLICY_VIOLATION(HttpStatus.BAD_REQUEST, "비밀번호 조건을 충족하지 않습니다."),
  INVALID_SEARCH_CRITERIA(HttpStatus.BAD_REQUEST, "검색 조건을 잘못 설정하였습니다."),
  PROFANITY_IN_NAME(HttpStatus.BAD_REQUEST, "이름에 비속어가 포함되어 있습니다."),
  DUPLICATE_NAME(HttpStatus.CONFLICT, "중복된 이름입니다."),
  // 계정 관련 오류
  EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
  EMAIL_ALREADY_EXISTS_OTHER_AUTH(HttpStatus.CONFLICT, "이미 다른 방식으로 인증되어 있는 이메일입니다."),
  INVALID_EMAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 틀립니다."),
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
  // OAuth 및 토큰 오류
  OAUTH_COMMUNICATION_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "OAuth 통신에 실패하였습니다."),
  INVALID_OAUTH_CODE(HttpStatus.BAD_REQUEST, "올바르지 않은 OAuth code입니다."),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "올바르지 않은 토큰입니다."),
  EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 엑세스 토큰입니다."),
  EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다."),
  TAMPERED_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED, "서명이 변형된 토큰입니다."),
  EMPTY_TOKEN_PROVIDED(HttpStatus.BAD_REQUEST, "요청에 토큰이 비어있습니다."),
  INSUFFICIENT_MEMBER_PERMISSION(HttpStatus.FORBIDDEN, "권한이 부족한 사용자입니다."),
  // 메일 전송 오류
  EMAIL_SENDING_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송에 실패하였습니다."),
  EMAIL_VERIFIED_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "메일 인증에 실패하였습니다."),
  // 프로세스 오류
  FAIL_PROCEED(HttpStatus.INTERNAL_SERVER_ERROR, "프로세스 실행중 문제가 발생하였습니다."),

  //마이페이지
  EMPTY_NICKNAME(HttpStatus.BAD_REQUEST, "닉네임 칸이 비어있습니다."),
  NICKNAME_DUPLICATE(HttpStatus.BAD_REQUEST, "닉네임 중복!"),

  // 정상
  SUCCESS(HttpStatus.OK, "SUCCESS");

  private final HttpStatus httpStatus;
  private final String message;
}
