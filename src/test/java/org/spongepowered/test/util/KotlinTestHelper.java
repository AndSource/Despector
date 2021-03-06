/*
 * The MIT License (MIT)
 *
 * Copyright (c) Despector <https://despector.voxelgenesis.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.test.util;

import org.spongepowered.despector.ast.SourceSet;
import org.spongepowered.despector.ast.type.MethodEntry;
import org.spongepowered.despector.ast.type.TypeEntry;
import org.spongepowered.despector.config.LibraryConfiguration;
import org.spongepowered.despector.decompiler.Decompilers;
import org.spongepowered.despector.emitter.Emitters;
import org.spongepowered.despector.emitter.format.EmitterFormat;
import org.spongepowered.despector.emitter.java.JavaEmitterContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

public class KotlinTestHelper {

    private static final SourceSet DUMMY_SOURCE_SET = new SourceSet();

    public static String getMethodAsString(byte[] data, String method_name) {
        LibraryConfiguration.quiet = false;
        LibraryConfiguration.parallel = false;
        LibraryConfiguration.force_lang = true;
        TypeEntry type = null;
        try {
            type = Decompilers.KOTLIN.decompile(new ByteArrayInputStream(data), DUMMY_SOURCE_SET);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MethodEntry method = type.getStaticMethod(method_name);
        return getMethodAsString(type, method);
    }

    public static String getMethodAsString(TypeEntry type, MethodEntry method) {
        StringWriter writer = new StringWriter();
        JavaEmitterContext emitter = new JavaEmitterContext(writer, EmitterFormat.defaults());
        Emitters.KOTLIN.setup(emitter);
        emitter.setType(type);
        emitter.emit(method);
        emitter.flush();
        return writer.toString();
    }
}
