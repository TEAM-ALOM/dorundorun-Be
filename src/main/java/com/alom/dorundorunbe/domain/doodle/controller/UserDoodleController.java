package com.alom.dorundorunbe.domain.doodle.controller;

import com.alom.dorundorunbe.domain.doodle.domain.Doodle;
import com.alom.dorundorunbe.domain.doodle.domain.UserDoodleStatus;
import com.alom.dorundorunbe.domain.doodle.service.UserDoodleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userDoodle/")
public class UserDoodleController implements UserDoodleControllerDocs{

    private final UserDoodleService userDoodleService;

    @GetMapping("{userId}/doodles/top-10")
    public ResponseEntity<List<Doodle>> getTop10DoodlePointsForUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userDoodleService.getTop10DoodlePointsForUser(userId));
    }

    @GetMapping("{userId}/doodles/{doodleId}/goal-status")
    public ResponseEntity<UserDoodleStatus> isGoalAchieved(@PathVariable("userId") Long userId, @PathVariable("doodleId") Long doodleId){
        return ResponseEntity.ok(userDoodleService.isGoalAchieved(userId, doodleId));
    }

}
