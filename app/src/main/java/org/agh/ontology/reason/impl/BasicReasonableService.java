package org.agh.ontology.reason.impl;

import org.agh.ontology.reason.AmbientLight;
import org.agh.ontology.reason.Motion;
import org.agh.ontology.reason.ReasonableService;
import org.agh.ontology.reason.RingtoneVolume;
import org.agh.ontology.reason.ScreenBrightness;
import org.agh.ontology.reason.TimeOfDay;
import org.agh.ontology.reason.VibrationLevel;

import static org.agh.ontology.reason.RingtoneVolume.LoudRingtone;
import static org.agh.ontology.reason.RingtoneVolume.MediumRingtone;
import static org.agh.ontology.reason.RingtoneVolume.OffRingtone;
import static org.agh.ontology.reason.RingtoneVolume.QuietRingtone;
import static org.agh.ontology.reason.ScreenBrightness.HighScreenBrightness;
import static org.agh.ontology.reason.ScreenBrightness.HighestScreenBrightness;
import static org.agh.ontology.reason.ScreenBrightness.LowScreenBrightness;
import static org.agh.ontology.reason.ScreenBrightness.LowestScreenBrightness;
import static org.agh.ontology.reason.ScreenBrightness.MediumScreenBrightness;
import static org.agh.ontology.reason.VibrationLevel.OffVibration;
import static org.agh.ontology.reason.VibrationLevel.OnVibration;

/**
 * This service acts as OntologyReasonableService with "default-logic-test.owl" logic ontology.
 */
public class BasicReasonableService implements ReasonableService {

    @Override
    public ScreenBrightness getScreenBrightnessFor(AmbientLight ambientLight) {
        switch (ambientLight) {
            case HighestAmbientLight:
                return HighestScreenBrightness;
            case HighAmbientLight:
                return HighScreenBrightness;
            case MediumAmbientLight:
                return MediumScreenBrightness;
            case LowAmbientLight:
                return LowScreenBrightness;
            case LowestAmbientLight:
                return LowestScreenBrightness;

            default:
                return MediumScreenBrightness;
        }
    }

    @Override
    public VibrationLevel getVibrationLevelFor(Motion motion) {
        switch (motion) {
            case InMotion:
                return OffVibration;
            case Stationary:
                return OnVibration;

            default:
                return OnVibration;
        }
    }

    @Override
    public RingtoneVolume getRingtoneVolumeFor(TimeOfDay timeOfDay) {
        switch (timeOfDay) {
            case Night:
                return OffRingtone;
            case Morning:
                return LoudRingtone;
            case Noon:
                return MediumRingtone;
            case Evening:
                return QuietRingtone;

            default:
                return MediumRingtone;
        }
    }
}
