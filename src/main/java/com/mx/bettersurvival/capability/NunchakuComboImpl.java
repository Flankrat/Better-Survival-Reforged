package com.mx.bettersurvival.capability;

public class NunchakuComboImpl implements INunchakuCombo {

    private float comboPower;
    private int comboTime;
    private boolean isSpinning;

    @Override
    public float getComboPower() {
        return comboPower;
    }

    @Override
    public void setComboPower(float power) {
        this.comboPower = Math.min(power, maxPower());
        this.comboTime = 30;
    }

    /** Highest combo power: 1.0 (x2 hit) in the original mode, cap - start in per-hit scaling mode. */
    private static float maxPower() {
        var c = com.mx.bettersurvival.config.ModConfig.COMMON;
        if (!c.nunchakuComboScalesWeaponDamage.get())
            return 1.0F;
        return (float) Math.max(0.0, c.nunchakuComboCap.get() - c.nunchakuComboStart.get());
    }

    @Override
    public int getComboTime() {
        return comboTime;
    }

    @Override
    public void setComboTime(int time) {
        this.comboTime = time;
    }

    @Override
    public void countDown() {
        this.comboTime--;
        if (this.comboTime <= 0) {
            this.comboTime = 0;
            this.comboPower = 0;
        }
    }

    @Override
    public boolean isSpinning() {
        return isSpinning;
    }

    @Override
    public void setSpinning(boolean spinning) {
        this.isSpinning = spinning;
    }
}
