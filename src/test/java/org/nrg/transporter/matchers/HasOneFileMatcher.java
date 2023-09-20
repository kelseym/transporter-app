package org.nrg.transporter.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.nio.file.Files;
import java.nio.file.Path;

public class HasOneFileMatcher extends TypeSafeMatcher<Path> {

    @Override
    protected boolean matchesSafely(Path dir) {
        try {
            return Files.list(dir).filter(Files::isRegularFile).count() == 1;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a directory with exactly one file");
    }

    public static HasOneFileMatcher hasOneFile() {
        return new HasOneFileMatcher();
    }
}
