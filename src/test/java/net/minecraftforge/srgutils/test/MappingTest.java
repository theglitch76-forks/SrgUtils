/*
 * SRG Utils
 * Copyright (c) 2021
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.srgutils.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import net.minecraftforge.srgutils.IMappingFile;
import net.minecraftforge.srgutils.IMappingFile.Format;
import net.minecraftforge.srgutils.INamedMappingFile;

import static org.junit.jupiter.api.Assertions.*;

public class MappingTest {

    InputStream getStream(String name) {
        return MappingTest.class.getClassLoader().getResourceAsStream(name);
    }

    @Test
    void test() throws IOException {
        IMappingFile pg = IMappingFile.load(getStream("./installer.pg"));
        IMappingFile reverse = pg.reverse();
        for (Format f : Format.values()) {
            pg.write(Paths.get("./build/installer_out." + f.name().toLowerCase()), f, false);
            reverse.write(Paths.get("./build/installer_out_rev." + f.name().toLowerCase()), f, false);
        }
    }

    @Test
    void reverse() throws IOException {
        IMappingFile a = INamedMappingFile.load(getStream("./installer.pg")).getMap("right", "left");
        IMappingFile b = INamedMappingFile.load(getStream("./installer.pg")).getMap("left", "right").reverse();
        a.getClasses().forEach(ca -> {
            IMappingFile.IClass cb = b.getClass(ca.getOriginal());
            assertNotNull(cb, "Could not find class: " + ca);
            ca.getFields().forEach(fa -> {
                IMappingFile.IField fb = cb.getField(fa.getOriginal());
                assertNotNull(fb, "Could not find field: " + fa);
                assertEquals(fa.getMapped(), fb.getMapped(), "Fields did not match: " + fa + "{" + fa.getMapped() + " -> " + fb.getMapped() + "}");
            });
            ca.getMethods().forEach(ma -> {
                IMappingFile.IMethod mb = cb.getMethod(ma.getOriginal(), ma.getDescriptor());
                if (mb == null) {
                    //Assertions.assertNotNull(mb, "Could not find method: " + ma);
                    StringBuilder buf = new StringBuilder();
                    buf.append("Could not find method: " + ma);
                    cb.getMethods().forEach(m -> {
                        buf.append("\n  ").append(m.toString());
                    });
                    throw new IllegalArgumentException(buf.toString());
                }
                assertEquals(ma.getMapped(), mb.getMapped(), "Methods did not match: " + ma + "{" + ma.getMapped() + " -> " + mb.getMapped() + "}");
                assertEquals(ma.getMappedDescriptor(), mb.getMappedDescriptor(), "Method descriptors did not match: " + ma + "{" + ma.getMappedDescriptor() + " -> " + mb.getMappedDescriptor() + "}");
            });
        });
    }

    @Test
    void tinyV2Comments() throws IOException {
        IMappingFile map = INamedMappingFile.load(getStream("./tiny_v2.tiny")).getMap("left", "right");

        IMappingFile.IClass cls = map.getClass("Foo");
        assertNotNull(cls, "Missing class");
        assertEquals("Class Comment", cls.getMetadata().get("comment"));

        IMappingFile.IField fld = cls.getField("foo");
        assertNotNull(fld, "Missing field");
        assertEquals("Field Comment", fld.getMetadata().get("comment"));

        IMappingFile.IMethod mtd = cls.getMethod("foo", "()V");
        assertNotNull(mtd, "Missing method");
        assertEquals("Method Comment", mtd.getMetadata().get("comment"));
        assertNotNull(mtd.getParameters(), "Missing parameter collection");

        IMappingFile.ILvtMember par = mtd.getLvt().iterator().next();
        assertNotNull(par, "Missing Parameter");
        assertEquals("Param Comment", par.getMetadata().get("comment"));
    }
}
