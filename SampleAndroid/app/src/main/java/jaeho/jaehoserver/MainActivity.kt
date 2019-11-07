package jaeho.jaehoserver

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class MainActivity : AppCompatActivity() {

    private val api = Retrofit.Builder()
        .baseUrl(url)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(JaehoApi::class.java)

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            api.getSomething()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, t ->
                    Toast.makeText(this, data, Toast.LENGTH_SHORT).show()
                    text.text = data
                    if (t != null) error_text.text = t.message

                }.also { disposable.add(it) }
        }

    }

    companion object {
        const val url = "https://jaeho.dev/"
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }
}


interface JaehoApi {
    @GET("api/test/")
    fun getSomething(): Single<String>


}
