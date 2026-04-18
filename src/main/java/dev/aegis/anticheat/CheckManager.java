package dev.aegis.anticheat;

import dev.aegis.AegisAC;
import dev.aegis.anticheat.checks.combat.*;
import dev.aegis.anticheat.checks.interaction.*;
import dev.aegis.anticheat.checks.movement.*;
import dev.aegis.anticheat.checks.packet.*;
import dev.aegis.anticheat.listener.MovementListener;
import dev.aegis.anticheat.listener.CombatListener;
import dev.aegis.anticheat.listener.InteractionListener;

public class CheckManager {

    private final AegisAC plugin;
    private MovementListener movementListener;
    private CombatListener combatListener;
    private InteractionListener interactionListener;
    private FlyCheck flyCheck;
    private SpeedCheck speedCheck;
    private NoFallCheck noFallCheck;
    private JesusCheck jesusCheck;
    private StepCheck stepCheck;
    private AccelerationCheck accelerationCheck;
    private KillAuraCheck killAuraCheck;
    private ReachCheck reachCheck;
    private AutoClickerCheck autoClickerCheck;
    private AimAssistCheck aimAssistCheck;
    private FastBreakCheck fastBreakCheck;
    private FastPlaceCheck fastPlaceCheck;
    private ScaffoldCheck scaffoldCheck;
    private TimerCheck timerCheck;
    private BadPacketsCheck badPacketsCheck;

    public CheckManager(AegisAC plugin) {
        this.plugin = plugin;
        initChecks();
    }

    private void initChecks() {
        flyCheck = new FlyCheck(plugin);
        speedCheck = new SpeedCheck(plugin);
        noFallCheck = new NoFallCheck(plugin);
        jesusCheck = new JesusCheck(plugin);
        stepCheck = new StepCheck(plugin);
        accelerationCheck = new AccelerationCheck(plugin);
        killAuraCheck = new KillAuraCheck(plugin);
        reachCheck = new ReachCheck(plugin);
        autoClickerCheck = new AutoClickerCheck(plugin);
        aimAssistCheck = new AimAssistCheck(plugin);
        fastBreakCheck = new FastBreakCheck(plugin);
        fastPlaceCheck = new FastPlaceCheck(plugin);
        scaffoldCheck = new ScaffoldCheck(plugin);
        timerCheck = new TimerCheck(plugin);
        badPacketsCheck = new BadPacketsCheck(plugin);

        movementListener = new MovementListener(plugin, flyCheck, speedCheck,
                noFallCheck, jesusCheck, stepCheck, accelerationCheck, timerCheck);
        combatListener = new CombatListener(plugin, killAuraCheck, reachCheck,
                autoClickerCheck, aimAssistCheck);
        interactionListener = new InteractionListener(plugin, fastBreakCheck,
                fastPlaceCheck, scaffoldCheck);

        plugin.getServer().getPluginManager().registerEvents(movementListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(combatListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(interactionListener, plugin);
    }

    public void reload() { initChecks(); }

    public MovementListener getMovementListener() { return movementListener; }
    public FlyCheck getFlyCheck() { return flyCheck; }
    public SpeedCheck getSpeedCheck() { return speedCheck; }
    public KillAuraCheck getKillAuraCheck() { return killAuraCheck; }
    public ReachCheck getReachCheck() { return reachCheck; }
    public AutoClickerCheck getAutoClickerCheck() { return autoClickerCheck; }
    public AimAssistCheck getAimAssistC
