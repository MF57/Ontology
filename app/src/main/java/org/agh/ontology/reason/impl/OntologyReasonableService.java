package org.agh.ontology.reason.impl;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;

import org.agh.ontology.reason.AmbientLight;
import org.agh.ontology.reason.Motion;
import org.agh.ontology.reason.ReasonableService;
import org.agh.ontology.reason.RingtoneVolume;
import org.agh.ontology.reason.ScreenBrightness;
import org.agh.ontology.reason.TimeOfDay;
import org.agh.ontology.reason.VibrationLevel;

import java.io.InputStream;

public class OntologyReasonableService implements ReasonableService {

    private final Property hasScreenBrightnessProperty;
    private final Property hasRingtoneVolume;
    private final Property hasVibrationLevel;

    private final InfModel infModel;

    public OntologyReasonableService(InputStream domainOwlStream, InputStream logicOwlStream) {
        Model domain = ModelFactory.createDefaultModel().read(domainOwlStream, "");
        Model logic = ModelFactory.createDefaultModel().read(logicOwlStream, "");
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner().bindSchema(domain);

        this.infModel = ModelFactory.createInfModel(reasoner, logic);

        this.hasScreenBrightnessProperty = infModel.getProperty("hasScreenBrightness");
        this.hasRingtoneVolume = infModel.getProperty("hasRingtoneVolume");
        this.hasVibrationLevel = infModel.getProperty("hasVibrationLevel");
    }

    @Override
    public ScreenBrightness getScreenBrightnessFor(AmbientLight ambientLight) {
        return ScreenBrightness.valueOf(infModel.listObjectsOfProperty(
                infModel.getResource(ambientLight.name()),
                hasScreenBrightnessProperty
        ).next().toString());
    }

    @Override
    public VibrationLevel getVibrationLevelFor(Motion motion) {
        return VibrationLevel.valueOf(infModel.listObjectsOfProperty(
                infModel.getResource(motion.name()),
                hasVibrationLevel
        ).next().toString());
    }

    @Override
    public RingtoneVolume getRingtoneVolumeFor(TimeOfDay timeOfDay) {
        return RingtoneVolume.valueOf(infModel.listObjectsOfProperty(
                infModel.getResource(timeOfDay.name()),
                hasRingtoneVolume
        ).next().toString());
    }

}
