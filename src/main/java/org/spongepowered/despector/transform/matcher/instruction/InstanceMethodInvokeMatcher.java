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
package org.spongepowered.despector.transform.matcher.instruction;

import org.spongepowered.despector.ast.insn.Instruction;
import org.spongepowered.despector.ast.stmt.invoke.InstanceMethodInvoke;
import org.spongepowered.despector.transform.matcher.InstructionMatcher;
import org.spongepowered.despector.transform.matcher.MatchContext;

import java.util.HashMap;
import java.util.Map;

/**
 * A matcher for instance method invokes.
 */
public class InstanceMethodInvokeMatcher implements InstructionMatcher<InstanceMethodInvoke> {

    private InstructionMatcher<?> callee;
    private boolean unwrap;
    private String owner;
    private String name;
    private String desc;
    private Map<Integer, InstructionMatcher<?>> parameters;

    InstanceMethodInvokeMatcher(InstructionMatcher<?> callee, String owner, String name, String desc, boolean unwrap,
            Map<Integer, InstructionMatcher<?>> parameters) {
        this.callee = callee == null ? InstructionMatcher.ANY : callee;
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this.unwrap = unwrap;
        this.parameters = parameters;
    }

    @Override
    public InstanceMethodInvoke match(MatchContext ctx, Instruction insn) {
        if (!(insn instanceof InstanceMethodInvoke)) {
            return null;
        }
        InstanceMethodInvoke invoke = (InstanceMethodInvoke) insn;
        if (this.owner != null && !this.owner.equals(invoke.getOwner())) {
            return null;
        }
        if (this.name != null && !this.name.equals(invoke.getMethodName())) {
            return null;
        }
        if (this.desc != null && !this.desc.equals(invoke.getMethodDescription())) {
            return null;
        }
        Instruction callee = invoke.getCallee();
        if (this.unwrap) {
            callee = InstructionMatcher.unwrapCast(callee);
        }
        if (!this.callee.matches(ctx, callee)) {
            return null;
        }
        Instruction[] params = invoke.getParameters();
        for (Map.Entry<Integer, InstructionMatcher<?>> e : this.parameters.entrySet()) {
            if (e.getKey() >= params.length) {
                return null;
            }
            if (!e.getValue().matches(ctx, params[e.getKey()])) {
                return null;
            }
        }
        return invoke;
    }

    /**
     * A matcher builder.
     */
    public static class Builder {

        private InstructionMatcher<?> callee;
        private String owner;
        private String name;
        private String desc;
        private boolean unwrap;
        private Map<Integer, InstructionMatcher<?>> parameters = new HashMap<>();

        public Builder() {
            reset();
        }

        public Builder callee(InstructionMatcher<?> callee) {
            this.callee = callee;
            return this;
        }

        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder desc(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder autoUnwrap() {
            this.unwrap = true;
            return this;
        }

        public Builder param(int index, InstructionMatcher<?> matcher) {
            this.parameters.put(index, matcher);
            return this;
        }

        /**
         * Resets this builder.
         */
        public Builder reset() {
            this.callee = null;
            this.owner = null;
            this.name = null;
            this.desc = null;
            this.unwrap = false;
            this.parameters.clear();
            return this;
        }

        public InstanceMethodInvokeMatcher build() {
            return new InstanceMethodInvokeMatcher(this.callee, this.owner, this.name, this.desc, this.unwrap, this.parameters);
        }

    }

}
