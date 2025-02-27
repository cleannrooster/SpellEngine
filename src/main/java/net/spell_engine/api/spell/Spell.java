package net.spell_engine.api.spell;

import net.spell_engine.api.render.LightEmission;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchool;
import org.jetbrains.annotations.Nullable;

public class Spell {
    // Structure
    public SpellSchool school;
    public float range = 50;

    // An arbitrary group to group spells by
    // Spells with the same group override each other, prioritized by tier
    // For primary spells (such as hard bound spells of weapons, and first spells of spell books) the recommended group is "primary"
    @Nullable public String group;
    // The rank of the spell, used to determine which spell to use when multiple spells with the same `group` are available
    public int rank = 1;

    public Learn learn = new Learn();
    public static class Learn { public Learn() {}
        public int tier = 1;
        public int level_cost_per_tier = 3;
        public int level_requirement_per_tier = 10;
    }

    public enum Mode { CAST, ITEM_USE }
    public Mode mode = Mode.CAST;

    public Cast cast = new Cast();
    public static class Cast { public Cast() { }
        public boolean haste_affected = true;
        public float duration = 0;
        public int channel_ticks = 0;
        public String animation;
        public boolean animates_ranged_weapon = false;
        /// Default `0.2` matches the same as movement speed during vanilla item usage (such as bow)"
        public float movement_speed = 0.2F;
        public Sound start_sound;
        public Sound sound;
        public ParticleBatch[] particles = new ParticleBatch[]{};
    }

    public ItemUse item_use = new ItemUse();
    public static class ItemUse { public ItemUse() { }
        public boolean shows_item_as_icon = false;
        public boolean requires_offhand_item = false;
    }

    @Nullable public ArrowPerks arrow_perks = null;
    public static class ArrowPerks { public ArrowPerks() { }
        public float damage_multiplier = 1F;
        public float velocity_multiplier = 1F;
        public boolean bypass_iframes = false;
        public int iframe_to_set = 0;
        public boolean skip_arrow_damage = false;
        public int pierce = 0;
        public float knockback = 1;
        @Nullable public ParticleBatch[] travel_particles;
        @Nullable public ProjectileModel override_render;
    }

    public Release release;
    public static class Release { public Release() { }
        public Target target;
        public boolean custom_impact = false;
        public static class Target { public Target() { }
            public Type type;
            public enum Type {
                AREA, BEAM, CURSOR, SELF,

                // To be refactored into `Action` in the future
                PROJECTILE, METEOR, CLOUD, SHOOT_ARROW
            }

            public Area area;
            public static class Area { public Area() { }
                public enum DropoffCurve { NONE, SQUARED }
                public DropoffCurve distance_dropoff = DropoffCurve.NONE;
                public float horizontal_range_multiplier = 1F;
                public float vertical_range_multiplier = 1F;
                public float angle_degrees = 0F;
                public boolean include_caster = false;
            }

            public Beam beam;
            public static class Beam { public Beam() { }
                public String texture_id = "textures/entity/beacon_beam.png";
                public long color_rgba = 0xFFFFFFFF;
                public float width = 0.1F;
                public float flow = 1;
                public ParticleBatch[] block_hit_particles = new ParticleBatch[]{};
            }

            // Populate either `cloud` or `clouds` but not both
            public Cloud cloud;
            public Cloud[] clouds = new Cloud[]{};
            public static class Cloud { public Cloud() { }
                // Custom entity type id to spawn, must be a subclass of `SpellCloud`
                @Nullable public String entity_type_id;
                public AreaImpact volume = new AreaImpact();
                public float time_to_live_seconds = 0;

                /// The number of ticks between looking for targets and trying to apply impact
                public int impact_tick_interval = 5;
                public int delay_ticks = 0;
                public EntityPlacement placement = new EntityPlacement();
                @Nullable public Sound presence_sound;
                public ClientData client_data = new ClientData();
                public static class ClientData {
                    public int light_level = 0;
                    public ParticleBatch[] particles = new ParticleBatch[]{};
                    public ProjectileModel model;
                }
                public Spawn spawn = new Spawn();
                public static class Spawn {
                    public Sound sound;
                    public ParticleBatch[] particles = new ParticleBatch[]{};
                }
            }

            public Cursor cursor;
            public static class Cursor { public Cursor() { }
                public boolean use_caster_as_fallback = false;
            }

            public ShootProjectile projectile;
            public static class ShootProjectile {
                public boolean inherit_shooter_velocity = false;
                /// Launch properties of the spell projectile
                public LaunchProperties launch_properties = new LaunchProperties();
                /// The projectile to be launched
                public ProjectileData projectile;
            }

            public Meteor meteor;
            public static class Meteor { public Meteor() { }
                /// Determines whether the it can be casted on the ground, without a targeted entity
                public boolean requires_entity = false;
                /// How high the falling projectile is launched from compared to the position of the target
                public float launch_height = 10;
                public int offset_requires_sequence = 1;
                public int divergence_requires_sequence = 1;
                public int follow_target_requires_sequence = -1;
                /// How far horizontally the falling projectile is launched from the target
                public float launch_radius = 0;
                /// Launch properties of the falling projectile
                public LaunchProperties launch_properties = new LaunchProperties();
                /// The projectile to be launched
                public ProjectileData projectile;
            }

            public ShootArrow shoot_arrow;
            public static class ShootArrow { public ShootArrow() { }
                public boolean consume_arrow = true;
                public float divergence = 5F;
                public boolean arrow_critical_strike = true;
                /// Launch properties of the arrow
                /// (vanilla default velocity for crossbows is 3.15)
                public LaunchProperties launch_properties = new LaunchProperties().velocity(3.15F);
            }
        }
        public String animation;
        public ParticleBatch[] particles;
        public Sound sound;
    }

    public Impact[] impact;
    public static class Impact { public Impact() { }
        public Action action;
        /// Magic school of this specific impact, if null then spell school is used
        @Nullable public SpellSchool school;
        public static class Action { public Action() { }
            public Type type;
            public boolean apply_to_caster = false;
            public float min_power = 1;
            public enum Type {
                DAMAGE, HEAL, STATUS_EFFECT, FIRE, SPAWN, TELEPORT
            }
            public Damage damage;
            public static class Damage { public Damage() { }
                public boolean bypass_iframes = true;
                public float spell_power_coefficient = 1;
                public float base_power = 0;
                public float knockback = 1;
            }
            public Heal heal;
            public static class Heal { public Heal() { }
                public float spell_power_coefficient = 1;
                public float base_heal = 0;
            }
            public StatusEffect status_effect;
            public static class StatusEffect { public StatusEffect() { }
                public String effect_id;
                public float duration = 10;
                public int amplifier = 0;
                public float amplifier_power_multiplier = 0;
                public ApplyMode apply_mode = ApplyMode.SET;
                public enum ApplyMode { SET, ADD }
                public ApplyLimit apply_limit;
                public static class ApplyLimit { public ApplyLimit() { }
                    public float health_base = 0;
                    public float spell_power_multiplier = 0;
                }
                public boolean show_particles = true;
            }
            public Fire fire;
            public static class Fire { public Fire() { }
                // Entity.java - Notice `% 20` - tick offset is used to avoid instant hits
                // if (this.fireTicks % 20 == 0 && !this.isInLava()) {
                //    this.damage(DamageSource.ON_FIRE, 1.0f);
                // }
                public int duration = 2;
                public int tick_offset = 10;
            }

            // Populate either `spawn` or `spawns` but not both
            public Spawn spawn;
            public Spawn[] spawns = new Spawn[]{};
            public static class Spawn {
                public String entity_type_id;
                public int time_to_live_seconds = 0;
                public int delay_ticks = 0;
                public EntityPlacement placement = new EntityPlacement();
            }

            public Teleport teleport;
            public static class Teleport { public Teleport() { }
                public enum Mode { FORWARD, BEHIND_TARGET }
                public Mode mode;
                public int required_clearance_block_y = 1;
                public TargetHelper.Intent intent = TargetHelper.Intent.HELPFUL;
                public Forward forward;
                public static class Forward { public Forward() { }
                    public float distance = 10;
                }
                public BehindTarget behind_target;
                public static class BehindTarget { public BehindTarget() { }
                    public float distance = 1.5F;
                }
                @Nullable public ParticleBatch[] depart_particles;
                @Nullable public ParticleBatch[] arrive_particles;
            }
        }

        public ParticleBatch[] particles = new ParticleBatch[]{};
        public Sound sound;
    }
    /// Apply this impact to other entities nearby
    @Nullable
    public AreaImpact area_impact;

    public Cost cost = new Cost();
    public static class Cost { public Cost() { }
        public float exhaust = 0.1F;
        public String item_id;
        public boolean consume_item = true;
        public String effect_id;
        public int durability = 1;
        public float cooldown_duration = 0;
        public boolean cooldown_proportional = false;
        public boolean cooldown_haste_affected = true;
    }

    // MARK: Shared structures (used from multiple places in the spell structure)

    public static class AreaImpact { public AreaImpact() { }
        public float radius = 1F;
        public ExtraRadius extra_radius = new ExtraRadius();
        public static class ExtraRadius {
            public float power_coefficient = 0;
            public float power_cap = 0;
        }
        public Release.Target.Area area = new Release.Target.Area();
        public ParticleBatch[] particles = new ParticleBatch[]{};
        @Nullable
        public Sound sound;

        public float combinedRadius(SpellPower.Result power) {
            return radius + extra_radius.power_coefficient * (float) Math.min(extra_radius.power_cap, power.baseValue());
        }
    }

    public static class LaunchProperties { public LaunchProperties() { }
        /// Initial velocity of the projectile
        public float velocity = 1F;
        /// How many additional projectiles are spawned after launch
        public int extra_launch_count = 0;
        /// How many ticks after launch additional projectiles are spawned
        public int extra_launch_delay = 2;
        /// The sound to play on launch
        @Nullable public Sound sound;

        public LaunchProperties velocity(float value) {
            this.velocity = value;
            return this;
        }
        public LaunchProperties copy() {
            LaunchProperties copy = new LaunchProperties();
            copy.velocity = this.velocity;
            copy.extra_launch_count = this.extra_launch_count;
            copy.extra_launch_delay = this.extra_launch_delay;
            copy.sound = this.sound != null ? this.sound.copy() : null;
            return copy;
        }
    }

    public static class ProjectileData { public ProjectileData() { }
        public float divergence = 0;
        public float homing_angle = 1F;
        /// The frequency of playing the travel sound in ticks
        public int travel_sound_interval = 20;
        @Nullable public Sound travel_sound;

        public Perks perks = new Perks();
        public static class Perks { Perks() { }
            /// How many entities projectile can ricochet to
            public int ricochet = 0;
            /// How far ricochet can look for a target
            public float ricochet_range = 5;
            /// How many times projectile can bounce off a wall
            public int bounce = 0;
            /// Whether ricochet and bounce should be decremented together
            public boolean bounce_ricochet_sync = true;
            /// How many entities projectile can go through
            public int pierce = 0;
            /// How many additional projectiles are spawned on impact
            public int chain_reaction_size = 0;
            /// How many generation of chain reaction projectiles are spawned
            public int chain_reaction_triggers = 1;
            /// How many more projectiles are spawned from chain reaction of a spawned projectile
            public int chain_reaction_increment = -1;

            public Perks copy() {
                Perks copy = new Perks();
                copy.ricochet = this.ricochet;
                copy.ricochet_range = this.ricochet_range;
                copy.bounce = this.bounce;
                copy.bounce_ricochet_sync = this.bounce_ricochet_sync;
                copy.pierce = this.pierce;
                copy.chain_reaction_size = this.chain_reaction_size;
                copy.chain_reaction_triggers = this.chain_reaction_triggers;
                copy.chain_reaction_increment = this.chain_reaction_increment;
                return copy;
            }
        }

        public Client client_data;
        public static class Client { public Client() { }
            /// Ambient light level of the projectile, like players holding torches
            /// Requires `LambDynamicLights` to be installed
            /// Example values:
            /// 14 - torch
            /// 10 - soul torch
            public int light_level = 0;
            public ParticleBatch[] travel_particles;
            public ProjectileModel model;
        }
    }

    public static class ProjectileModel { public ProjectileModel() { }
        public boolean use_held_item = false;
        public String model_id;
        public LightEmission light_emission = LightEmission.GLOW;
        public float scale = 1F;
        public float rotate_degrees_per_tick = 2F;
        public float rotate_degrees_offset = 0;
        public Orientation orientation = Orientation.TOWARDS_MOTION;
        public enum Orientation {
            TOWARDS_CAMERA, TOWARDS_MOTION, ALONG_MOTION
        }
    }

    public static class EntityPlacement { public EntityPlacement() { }
        // If greater than 0, the entity will be placed at the caster's look direction, by this many blocks
        public boolean force_onto_ground = true;
        public float location_offset_by_look = 0;
        public float location_yaw_offset = 0;
        public boolean apply_yaw = false;
        public boolean apply_pitch = false;
        public float location_offset_x = 0;
        public float location_offset_y = 0;
        public float location_offset_z = 0;
    }
}
