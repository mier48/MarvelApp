package com.albertomier.marveldemo.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albertomier.marveldemo.core.Utils
import com.albertomier.marveldemo.domain.AddHeroToFavorite
import com.albertomier.marveldemo.domain.GetHeroes
import com.albertomier.marveldemo.domain.GetHeroesFromDatabase
import com.albertomier.marveldemo.domain.RemoveHeroFromFavorite
import com.albertomier.marveldemo.domain.model.Hero
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeroesViewModel @Inject constructor(
    private val getHeroes: GetHeroes,
    private val addHeroToFavorite: AddHeroToFavorite,
    private val removeHeroFromFavorite: RemoveHeroFromFavorite,
    private val getHeroesFromDatabase: GetHeroesFromDatabase
) : ViewModel() {

    var heroesModel = MutableLiveData<List<Hero>>()
    val isLoading = MutableLiveData<Boolean>()

    fun onCreate(limit: Int) {
        viewModelScope.launch {
            if (limit == 10) {
                isLoading.postValue(true)
            }

            val result = if (Utils.isOnline()) {
                getHeroes(limit)
            } else {
                getHeroesFromDatabase(limit)
            }

            if (!result.isNullOrEmpty()) {
                heroesModel.postValue(result)
            }

            if (limit == 10) {
                isLoading.postValue(false)
            }
        }
    }

    fun addToFavorite(hero: Hero, limit: Int) {
        viewModelScope.launch {
            isLoading.postValue(true)

            addHeroToFavorite(hero.id)

            val result = getHeroes(limit)

            if (!result.isNullOrEmpty()) {
                heroesModel.postValue(result)
            }

            isLoading.postValue(false)
        }
    }

    fun deleteFavorite(hero: Hero, limit: Int) {
        viewModelScope.launch {
            isLoading.postValue(true)

            removeHeroFromFavorite(hero.id)

            val result = getHeroes(limit)

            if (!result.isNullOrEmpty()) {
                heroesModel.postValue(result)
            }

            isLoading.postValue(false)
        }
    }
}