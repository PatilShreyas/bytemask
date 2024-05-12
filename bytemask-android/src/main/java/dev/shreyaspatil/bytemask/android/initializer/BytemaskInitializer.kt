/**
 * Copyright 2024 Shreyas Patil
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.shreyaspatil.bytemask.android.initializer

import android.content.Context
import androidx.startup.Initializer
import dev.shreyaspatil.bytemask.android.AndroidBytemask
import dev.shreyaspatil.bytemask.core.Bytemask

/** Initializes ByteMask at the application startup */
class BytemaskInitializer : Initializer<Bytemask> {
    override fun create(context: Context): Bytemask {
        AndroidBytemask.init(context)
        return Bytemask.getInstance()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
