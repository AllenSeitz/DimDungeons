package com.catastrophe573.dimdungeons.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SecretBellDataComponent
{
    public static SecretBellDataComponentRecord getDefaultKeyData()
    {
        return new SecretBellDataComponentRecord(
                1,   // upgrade
                -1,  // secret_x
                -1,  // secret_y
                -1   // secret_z
        );
    }

    public static final Codec<SecretBellDataComponentRecord> SECRET_BELL_DCR_CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("upgrade").forGetter(SecretBellDataComponentRecord::upgrade),
            Codec.INT.fieldOf("secret_x").forGetter(SecretBellDataComponentRecord::secret_x),
            Codec.INT.fieldOf("secret_y").forGetter(SecretBellDataComponentRecord::secret_y),
            Codec.INT.fieldOf("secret_z").forGetter(SecretBellDataComponentRecord::secret_z)
        ).apply(instance, SecretBellDataComponentRecord::new)
    );
}