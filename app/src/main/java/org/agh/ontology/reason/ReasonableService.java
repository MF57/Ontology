package org.agh.ontology.reason;

public interface ReasonableService {

    ScreenBrightness getScreenBrightnessFor(AmbientLight ambientLight);

    VibrationLevel getVibrationLevelFor(Motion motion);

    RingtoneVolume getRingtoneVolumeFor(TimeOfDay timeOfDay);

}
