package com.edgn.mixin.mixins.events;

import com.edgn.event.EventManager;
import com.edgn.event.listeners.ScoreboardListener;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.jetbrains.annotations.Nullable;

@Mixin(Scoreboard.class)
public class ScoreboardMixin {

    @Inject(method = "addObjective", at = @At("HEAD"), cancellable = true)
    private void onAddObjective(String name, ScoreboardCriterion criterion, Text displayName, 
                               ScoreboardCriterion.RenderType renderType, boolean displayAutoUpdate, 
                               @Nullable NumberFormat numberFormat, CallbackInfoReturnable<ScoreboardObjective> cir) {
        
        ScoreboardListener.ScoreboardObjectiveAddEvent event = 
            new ScoreboardListener.ScoreboardObjectiveAddEvent(name, criterion, displayName, renderType);
        EventManager.fire(event);
        
        if (event.isCancelled()) {
            cir.cancel();
        }
    }

    @Inject(method = "removeObjective", at = @At("HEAD"), cancellable = true)
    private void onRemoveObjective(ScoreboardObjective objective, CallbackInfo ci) {
        ScoreboardListener.ScoreboardObjectiveRemoveEvent event = 
            new ScoreboardListener.ScoreboardObjectiveRemoveEvent(objective);
        EventManager.fire(event);
        
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "updateScore", at = @At("HEAD"), cancellable = true)
    protected void onUpdateScore(ScoreHolder scoreHolder, ScoreboardObjective objective, ScoreboardScore score, CallbackInfo ci) {
        ScoreboardListener.ScoreboardUpdateEvent event = 
            new ScoreboardListener.ScoreboardUpdateEvent(
                (Scoreboard) (Object) this,
                scoreHolder.getNameForScoreboard(),
                objective.getName(),
                score.getScore(),
                score.getDisplayText()
            );
        EventManager.fire(event);
        
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "setObjectiveSlot", at = @At("HEAD"), cancellable = true)
    private void onSetObjectiveSlot(ScoreboardDisplaySlot slot, @Nullable ScoreboardObjective objective, CallbackInfo ci) {
        ScoreboardListener.ScoreboardSlotChangeEvent event = 
            new ScoreboardListener.ScoreboardSlotChangeEvent(slot, objective);
        EventManager.fire(event);
        
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "addTeam", at = @At("HEAD"), cancellable = true)
    private void onAddTeam(String name, CallbackInfoReturnable<Team> cir) {
        ScoreboardListener.TeamAddEvent event = new ScoreboardListener.TeamAddEvent(name);
        EventManager.fire(event);
        
        if (event.isCancelled()) {
            cir.cancel();
        }
    }

    @Inject(method = "removeTeam", at = @At("HEAD"), cancellable = true)
    private void onRemoveTeam(Team team, CallbackInfo ci) {
        ScoreboardListener.TeamRemoveEvent event = new ScoreboardListener.TeamRemoveEvent(team);
        EventManager.fire(event);
        
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "addScoreHolderToTeam", at = @At("HEAD"), cancellable = true)
    private void onAddScoreHolderToTeam(String scoreHolderName, Team team, CallbackInfoReturnable<Boolean> cir) {
        ScoreboardListener.PlayerTeamJoinEvent event = 
            new ScoreboardListener.PlayerTeamJoinEvent(scoreHolderName, team);
        EventManager.fire(event);
        
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "removeScoreHolderFromTeam", at = @At("HEAD"), cancellable = true)
    private void onRemoveScoreHolderFromTeam(String scoreHolderName, Team team, CallbackInfo ci) {
        ScoreboardListener.PlayerTeamLeaveEvent event = 
            new ScoreboardListener.PlayerTeamLeaveEvent(scoreHolderName, team);
        EventManager.fire(event);
        
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}