package com.zenika.dorm.core.model.unit;

import com.zenika.dorm.core.modelnew.impl.DefaultDormOrigin;
import com.zenika.dorm.core.modelnew.impl.DormArtifact;
import com.zenika.dorm.core.modelnew.impl.DormModule;
import com.zenika.dorm.core.modelnew.impl.MavenOrigin;
import org.junit.Test;

/**
 * @author Lukasz Piliszczuk <lukasz.piliszczuk AT zenika.com>
 */
public class DormModuleTest {

    @Test
    public void test() {

        DefaultDormOrigin dormOrigin = new DefaultDormOrigin("zenika:foo");
        DormModule moduleFromDorm = new DormModule("1.0.0", dormOrigin);

        MavenOrigin mavenOrigin = new MavenOrigin("com.zenika", "foo");
        DormModule moduleFromMaven = new DormModule("1.0.0", mavenOrigin);

        DormArtifact artifact1 = new DormArtifact("zenika:foo:bar1", "1.0.0");
        DormArtifact artifact2 = new DormArtifact("zenika:foo:bar2", "1.0.0");

        moduleFromDorm.addArtifact(artifact1);
        moduleFromDorm.addArtifact(artifact2, "test");

        DormArtifact artifactMaven = new DormArtifact(moduleFromMaven.getQualifier(), moduleFromMaven.getVersion());

        moduleFromMaven.addArtifact(artifactMaven);

        describeModule(moduleFromDorm);
        describeModule(moduleFromMaven);
    }

    private void describeModule(DormModule module) {
        System.out.println("module : " + module);
        System.out.println("artifacts in the module :");
        for (DormArtifact artifact : module.getArtifacts()) {
            System.out.println(artifact);
        }
        System.out.println();
    }
}
