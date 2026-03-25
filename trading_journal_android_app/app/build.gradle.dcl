androidApplication {
    namespace = "org.example.app"

    dependencies {
        implementation("org.apache.commons:commons-text:1.11.0")
        implementation(project(":utilities"))
        implementation("com.google.android.material:material:1.11.0")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.recyclerview:recyclerview:1.3.2")
        implementation("androidx.cardview:cardview:1.0.0")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
        implementation("androidx.fragment:fragment-ktx:1.6.2")
        implementation("androidx.activity:activity-ktx:1.8.2")
        implementation("androidx.room:room-runtime:2.6.1")
        implementation("androidx.room:room-ktx:2.6.1")
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation("com.google.code.gson:gson:2.10.1")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
        implementation("androidx.viewpager2:viewpager2:1.0.0")
        implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
        implementation("androidx.core:core-ktx:1.12.0")
    }
}
