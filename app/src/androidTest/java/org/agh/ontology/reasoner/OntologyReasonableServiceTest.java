package org.agh.ontology.reasoner;


import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.agh.ontology.reason.ReasonableService;
import org.agh.ontology.reason.impl.OntologyReasonableService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;

import static org.agh.ontology.reason.AmbientLight.HighAmbientLight;
import static org.agh.ontology.reason.AmbientLight.HighestAmbientLight;
import static org.agh.ontology.reason.AmbientLight.LowAmbientLight;
import static org.agh.ontology.reason.AmbientLight.LowestAmbientLight;
import static org.agh.ontology.reason.AmbientLight.MediumAmbientLight;
import static org.agh.ontology.reason.Motion.InMotion;
import static org.agh.ontology.reason.Motion.Stationary;
import static org.agh.ontology.reason.RingtoneVolume.LoudRingtone;
import static org.agh.ontology.reason.RingtoneVolume.MediumRingtone;
import static org.agh.ontology.reason.RingtoneVolume.OffRingtone;
import static org.agh.ontology.reason.RingtoneVolume.QuietRingtone;
import static org.agh.ontology.reason.ScreenBrightness.HighScreenBrightness;
import static org.agh.ontology.reason.ScreenBrightness.HighestScreenBrightness;
import static org.agh.ontology.reason.ScreenBrightness.LowScreenBrightness;
import static org.agh.ontology.reason.ScreenBrightness.LowestScreenBrightness;
import static org.agh.ontology.reason.ScreenBrightness.MediumScreenBrightness;
import static org.agh.ontology.reason.TimeOfDay.Evening;
import static org.agh.ontology.reason.TimeOfDay.Morning;
import static org.agh.ontology.reason.TimeOfDay.Night;
import static org.agh.ontology.reason.TimeOfDay.Noon;
import static org.agh.ontology.reason.VibrationLevel.OffVibration;
import static org.agh.ontology.reason.VibrationLevel.OnVibration;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class OntologyReasonableServiceTest {

    private ReasonableService instance;

    @Before
    public void before() {
        try {
            InputStream domainOwlStream = InstrumentationRegistry.getTargetContext().getAssets().open("domain.owl");
            InputStream logicOwlStream = InstrumentationRegistry.getTargetContext().getAssets().open("default-logic.owl");
            this.instance = new OntologyReasonableService(domainOwlStream, logicOwlStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void lowestScreenBrightnessTest() {
        assertEquals(instance.getScreenBrightnessFor(LowestAmbientLight), LowestScreenBrightness);
    }

    @Test
    public void lowScreenBrightnessTest() {
        assertEquals(instance.getScreenBrightnessFor(LowAmbientLight), LowScreenBrightness);
    }

    @Test
    public void mediumScreenBrightnessTest() {
        assertEquals(instance.getScreenBrightnessFor(MediumAmbientLight), MediumScreenBrightness);
    }

    @Test
    public void highScreenBrightnessTest() {
        assertEquals(instance.getScreenBrightnessFor(HighAmbientLight), HighScreenBrightness);
    }

    @Test
    public void highestScreenBrightnessTest() {
        assertEquals(instance.getScreenBrightnessFor(HighestAmbientLight), HighestScreenBrightness);
    }


    @Test
    public void offRingtoneTest() {
        assertEquals(instance.getRingtoneVolumeFor(Night), OffRingtone);
    }

    @Test
    public void loudRingtoneTest() {
        assertEquals(instance.getRingtoneVolumeFor(Morning), LoudRingtone);
    }

    @Test
    public void mediumRingtoneTest() {
        assertEquals(instance.getRingtoneVolumeFor(Noon), MediumRingtone);
    }

    @Test
    public void quietRingtoneTest() {
        assertEquals(instance.getRingtoneVolumeFor(Evening), QuietRingtone);
    }


    @Test
    public void onVibrationTest() {
        assertEquals(instance.getVibrationLevelFor(InMotion), OnVibration);
    }

    @Test
    public void offVibrationTest() {
        assertEquals(instance.getVibrationLevelFor(Stationary), OffVibration);
    }

}
