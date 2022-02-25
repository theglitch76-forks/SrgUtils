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

package net.minecraftforge.srgutils;

public interface IMappingBuilder {
    public static IMappingBuilder create(String... names) {
        return new NamedMappingFile(names == null || names.length == 0 ? new String[] {"left", "right"} : names);
    }

    IPackage addPackage(String... names);
    IClass addClass(String... names);

    INamedMappingFile build();

    public interface IPackage {
        IPackage meta(String key, String value);
        IMappingBuilder build();
    }

    public interface IClass {
        IField field(String... names);
        IMethod method(String descriptor, String... names);
        IClass meta(String key, String value);
        IMappingBuilder build();
    }

    public interface IField {
        IField descriptor(String value);
        IField meta(String key, String value);
        IClass build();
    }

    public interface IMethod {
        IParameter parameter(int index, String... names);
        ILvtMember lvtMember(int index, String... names);
        IMethod meta(String key, String value);
        IClass build();
    }

    public interface IParameter {
        IParameter meta(String key, String value);
        IMethod build();
    }

    public interface ILvtMember {
        ILvtMember meta(String key, String value);
        IMethod build();
    }
}
