//package com.zenika.dorm.maven.helper;
//
//import com.zenika.dorm.core.model.old.DormArtifact;
//import org.sonatype.aether.artifact.Artifact;
//
//import com.zenika.dorm.maven.model.impl.DormMavenMetadata;
//
//public class MavenHelper {
//
//	public static DormArtifact<DormMavenMetadata> createDormArtifact(Artifact mavenArtifact) {
//
////		// maven metadata
////		DormMavenMetadata mavenMetadata = new DormMavenMetadata(mavenArtifact.getGroupId(), mavenArtifact.getArtifactId(),
////				mavenArtifact.getVersion(), mavenArtifact.getNeo4jExtension());
////
////		// dorm metadata
////		DormMetadata<DormMavenMetadata> dormMetadata = createDormMetadata(mavenMetadata);
////
////		// dorm file
////		DormFile dormFile = DormFileHelper.createDormFile(dormMetadata, mavenArtifact.getFile(),
////				mavenArtifact.getNeo4jExtension());
////
////		// dorm artifact
////		DormArtifact<DormMavenMetadata> dormArtifact = new DormArtifact<DormMavenMetadata>(dormMetadata, dormFile);
////		dormArtifact.setMetadata(dormMetadata);
////		dormArtifact.setFile(dormFile);
////
////		return dormArtifact;
//        return null;
//	}
//}
