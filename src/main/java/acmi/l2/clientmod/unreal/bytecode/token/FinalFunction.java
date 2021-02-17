/*
 * Copyright (c) 2021 acmi
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
package acmi.l2.clientmod.unreal.bytecode.token;

import acmi.l2.clientmod.io.UnrealPackage;
import acmi.l2.clientmod.io.annotation.Compact;
import acmi.l2.clientmod.unreal.UnrealRuntimeContext;
import acmi.l2.clientmod.unreal.annotation.ObjectRef;
import acmi.l2.clientmod.unreal.bytecode.token.annotation.FunctionParams;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class FinalFunction extends Token {
    public static final int OPCODE = 0x1c;

    @Compact
    @ObjectRef
    public int funcRef;
    @FunctionParams
    public Token[] params;

    public FinalFunction(int funcRef, Token... params) {
        this.funcRef = funcRef;
        this.params = params;
    }

    @Override
    protected int getOpcode() {
        return OPCODE;
    }

    @Override
    public String toString() {
        return "FinalFunction("
                + funcRef
                + (params == null || params.length == 0 ? ")" : Arrays.stream(params).map(Objects::toString).collect(Collectors.joining(", ", ", ", ")")));
    }

    @Override
    public String toString(UnrealRuntimeContext context) {
        UnrealPackage.Entry func = context.getUnrealPackage().objectReference(funcRef);
        String pref = "";

        if (context.getSerializer() != null) {
            UnrealPackage.Entry entryHolder = context.getEntry().getObjectPackage();
            UnrealPackage.Entry funcHolder = func.getObjectPackage();
            if (!Objects.equals(entryHolder.getObjectFullName(), funcHolder.getObjectFullName()) &&
                    context.getSerializer().isSubclass(funcHolder.getObjectFullName(), entryHolder.getObjectFullName())) {
                pref = context.getSerializer()
                        .getEnvironment()
                        .getExportEntry(entryHolder.getObjectFullName() + "." + func.getObjectName().getName(), "Core.Function"::equalsIgnoreCase)
                        .isPresent() ? "Super." : "";
            }
        }

        return pref + func.getObjectName().getName() + "(" + Arrays.stream(params).map(p -> p.toString(context)).collect(Collectors.joining(", ")) + ")";
    }
}
