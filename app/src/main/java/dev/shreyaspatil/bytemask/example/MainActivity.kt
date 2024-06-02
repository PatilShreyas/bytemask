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
package dev.shreyaspatil.bytemask.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import dev.shreyaspatil.bytemask.example.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            buttonApiKey.setOnClickListener { setSecret(BytemaskConfig.API_KEY) }
            buttonAccessToken.setOnClickListener { setSecret(BytemaskConfig.ACCESS_TOKEN) }
            buttonApiEndpoint.setOnClickListener { setSecret(BytemaskConfig.API_ENDPOINT) }
        }
    }

    private fun ActivityMainBinding.setSecret(secret: String) {
        textView.text = secret
    }
}
