package com.biz2.nowfloats.boost.updates.persistance.dao

import androidx.room.*
import com.boost.upgrades.data.model.YoutubeVideoModel
import io.reactivex.Single

@Dao
interface YoutubeVideoDao {
  @Query("SELECT * FROM YoutubeVideo")
  fun getYoutubeVideoItems(): Single<List<YoutubeVideoModel>>

  @Query("SELECT * FROM YoutubeVideo WHERE YoutubeVideo_key=:item_id")
  fun getYoutubeVideoItemById(item_id: String): Single<YoutubeVideoModel>

  @Query("SELECT COUNT(*) from YoutubeVideo")
  fun countYoutubeVideosItems(): Single<Int>

  @Query("DELETE FROM YoutubeVideo")
  fun emptyYoutubeVideos()

  @Insert
  fun insertToYoutubeVideos(vararg features: YoutubeVideoModel?)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAllYoutubeVideos(data: List<YoutubeVideoModel>)

  @Update
  fun updateYoutubeVideos(vararg YoutubeVideoModel: YoutubeVideoModel?)

  @Query("DELETE FROM YoutubeVideo WHERE YoutubeVideo_key=:itemId")
  fun deleteYoutubeVideosItem(vararg itemId: String)

}
