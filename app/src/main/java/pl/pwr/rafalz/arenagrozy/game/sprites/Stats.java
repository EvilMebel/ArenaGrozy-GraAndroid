package pl.pwr.rafalz.arenagrozy.game.sprites;

public class Stats {
    private final float hpStart;
    private final float strStart;
    private final float defStart;
    private float hp;
    private float str;
    private float def;
    private final float speed;

    private Stats(Builder builder) {
        this.hpStart = builder.hpStart;
        this.strStart = builder.strStart;
        this.defStart = builder.defStart;
        this.hp = builder.hp;
        this.str = builder.str;
        this.def = builder.def;
        this.speed = builder.speed;
    }

    public float getHpStart() {
        return hpStart;
    }

    public float getStrStart() {
        return strStart;
    }

    public float getDefStart() {
        return defStart;
    }

    public float getHp() {
        return hp;
    }

    public float getStr() {
        return str;
    }

    public float getDef() {
        return def;
    }

    public float getSpeed() {
        return speed;
    }

    public void setHp(float hp) {
        this.hp = hp;
    }

    public float getHpProc() {
        return hp / hpStart;
    }

    public boolean isFullHp() {
        return hp >= hpStart;
    }

    public static class Builder {
        private float hpStart = 1000;
        private float strStart = 2;
        private float defStart = 3;
        private float hp = 1000;
        private float str = 2;
        private float def = 3;
        private float speed;

        public Builder setHpStart(float hpStart) {
            this.hpStart = hpStart;
            return this;
        }

        public Builder setStrStart(float strStart) {
            this.strStart = strStart;
            return this;
        }

        public Builder setDefStart(float defStart) {
            this.defStart = defStart;
            return this;
        }

        public Builder setHp(float hp) {
            this.hp = hp;
            return this;
        }

        public Builder setStr(float str) {
            this.str = str;
            return this;
        }

        public Builder setDef(float def) {
            this.def = def;
            return this;
        }

        public Builder setStandardSpeed(float speedHigh) {
            this.speed = speedHigh;
            return this;
        }

        public Stats build() {
            return new Stats(this);
        }
    }


}