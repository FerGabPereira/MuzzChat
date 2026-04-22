package com.fernandopereira.muzzchat.di

import com.fernandopereira.muzzchat.data.local.MessageDatabase
import com.fernandopereira.muzzchat.data.local.DatabaseSeeder
import com.fernandopereira.muzzchat.data.repository.MessageRepositoryImpl
import com.fernandopereira.muzzchat.domain.repository.MessageRepository
import com.fernandopereira.muzzchat.domain.usecase.BuildChatItemsUseCase
import com.fernandopereira.muzzchat.presentation.chat.ChatViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule =
    module {
        single { MessageDatabase.build(androidContext()) }
        single { get<MessageDatabase>().messageDao() }
        single<MessageRepository> { MessageRepositoryImpl(dao = get(), seeder = get()) }
        single { DatabaseSeeder(dao = get()) }
        factory { BuildChatItemsUseCase() }
        viewModel { ChatViewModel(repository = get(), buildChatItemsUseCase = get()) }
    }
