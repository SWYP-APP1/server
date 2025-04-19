package com.swyp.futsal.domain.team.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swyp.futsal.domain.team.repository.TeamMemberRepository;
import com.swyp.futsal.domain.team.repository.TeamRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamMemberService {

    private final Logger logger = LoggerFactory.getLogger(TeamMemberService.class);
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    
    
}
