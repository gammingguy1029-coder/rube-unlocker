package com.rubecoder.rubeaurachecker.di

import android.content.Context
import com.rubecoder.rubeaurachecker.data.local.HistoryDataStore
import com.rubecoder.rubeaurachecker.data.local.PreferencesDataStore
import com.rubecoder.rubeaurachecker.data.local.RegistrationDataStore
import com.rubecoder.rubeaurachecker.data.remote.SupabaseAdminApi
import com.rubecoder.rubeaurachecker.data.remote.SupabaseAdminRemoteDataSource
import com.rubecoder.rubeaurachecker.data.remote.SupabaseApi
import com.rubecoder.rubeaurachecker.data.remote.SupabaseRemoteDataSource
import com.rubecoder.rubeaurachecker.domain.model.UserRegistration
import com.rubecoder.rubeaurachecker.domain.repository.AdminRepository
import com.rubecoder.rubeaurachecker.domain.repository.HistoryRepository
import com.rubecoder.rubeaurachecker.domain.repository.PreferencesRepository
import com.rubecoder.rubeaurachecker.domain.repository.RegistrationRepository
import com.rubecoder.rubeaurachecker.BuildConfig
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.Provides
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val builder = OkHttpClient.Builder()
            .addInterceptor(logging)

        // Add Supabase anon key header only if not in test mode
        if (!BuildConfig.SUPABASE_TEST_MODE) {
            builder.addInterceptor { chain ->
                val original = chain.request()
                val requestWithApiKey = original.newBuilder()
                    .header("apikey", BuildConfig.SUPABASE_ANON_KEY)
                    .method(original.method(), original.body())
                    .build()
                chain.proceed(requestWithApiKey)
            }
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(okHttpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SUPABASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                Json {
                    ignoreUnknownKeys = true
                }.asConverterFactory("application/json".toMediaType())
            )
    }

    @Provides
    @Singleton
    fun provideRetrofit(retrofitBuilder: Retrofit.Builder): Retrofit = retrofitBuilder.build()

    @Provides
    @Singleton
    fun provideSupabaseApi(retrofit: Retrofit): SupabaseApi {
        return retrofit.create(SupabaseApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSupabaseAdminApi(retrofit: Retrofit): SupabaseAdminApi {
        return retrofit.create(SupabaseAdminApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSupabaseRemoteDataSource(
        supabaseApi: SupabaseApi
    ): SupabaseRemoteDataSource {
        return SupabaseRemoteDataSource(supabaseApi)
    }

    @Provides
    @Singleton
    fun provideSupabaseAdminRemoteDataSource(
        supabaseAdminApi: SupabaseAdminApi
    ): SupabaseAdminRemoteDataSource {
        return SupabaseAdminRemoteDataSource(supabaseAdminApi)
    }

    @Provides
    @Singleton
    fun provideRegistrationRepository(
        @ApplicationContext context: Context,
        supabaseRemoteDataSource: SupabaseRemoteDataSource
    ): RegistrationRepository {
        return if (BuildConfig.SUPABASE_TEST_MODE) {
            RegistrationDataStore(context)
        } else {
            supabaseRemoteDataSource
        }
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(
        @ApplicationContext context: Context
    ): HistoryRepository {
        return HistoryDataStore(context)
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(
        @ApplicationContext context: Context
    ): PreferencesRepository {
        return PreferencesDataStore(context)
    }
}

/** Abstract Dagger module for bindings that need an abstract method. Kept separate
 *  from [NetworkModule] because `@Binds` methods must live in an abstract module. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindingsModule {

    @Binds
    @Singleton
    abstract fun bindAdminRepository(
        supabaseAdminRemoteDataSource: SupabaseAdminRemoteDataSource
    ): AdminRepository
}