package com.pipi.pipix.src.main

import android.content.Context
import android.media.MediaPlayer
import android.os.SystemClock.sleep
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import com.pipi.pipix.R
import com.pipi.pipix.data.PureViewModel
import com.pipi.pipix.src.main.Fragment.PureFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.abs

class PureTest(var buttonRight: Button,var buttonLeft:Button,var buttonCheck:Button,var textCount: TextView,var viewModel: PureViewModel,var context: Context) {

    private var r250: Int? = null
    private var r500: Int? = null
    private var r750: Int? = null
    private var r1000: Int? = null
    private var r2000: Int? = null
    private var r3000: Int? = null
    private var r4000: Int? = null
    private var r6000: Int? = null
    private var r8000: Int? = null

    private var l250: Int? = null
    private var l500: Int? = null
    private var l750: Int? = null
    private var l1000: Int? = null
    private var l2000: Int? = null
    private var l3000: Int? = null
    private var l4000: Int? = null
    private var l6000: Int? = null
    private var l8000: Int? = null

    private var rightDb = 0f
    private var leftDb = 0f
    private var currentDirec = 1
    private var currentDb = 30
    private var currentHz = 0
    private var firstTouch = true
    private var cantHear = false
    private var whileState = true
    private var result = mutableMapOf<Int,MutableMap<Int,Int>>()
    private var resultRight = mutableMapOf<Int,Int>()
    private var resultLeft = mutableMapOf<Int,Int>()
    private var dbSet = mutableSetOf<Int>()

    // 1차 테스트 음원 아이디 리스트
    private val resIdList1 = arrayListOf(R.raw.hz1000, R.raw.hz2000,R.raw.hz4000,R.raw.hz8000,R.raw.hz250,R.raw.hz500)
    private val dbList = arrayListOf(1000, 2000, 4000, 8000, 250, 500)
    // 2차 테스트 음원 아이디 리스트
    private val resIdList2 = arrayListOf(R.raw.hz750, R.raw.hz1500, R.raw.hz3000, R.raw.hz6000)
    private val dbList2 = arrayListOf(750, 1500, 3000, 6000)
    // 데시벨 맵
    private val dbMap = mutableMapOf<Int,Float>()
    //데시벨 맵핑
    init {
        for(i in 0..100 step 5) dbMap[i] = i/100f

        //우
        result[1] = resultRight

        //좌
        result[0] = resultLeft
    }




    fun doTest(){

        for(i in 0..5){ // start of first test For

            // 해당 헤르츠의 음원 준비
            val mediaPlayer = MediaPlayer.create(context, resIdList1[i])

            //해당 헤르츠 테스트 초기 세팅
            currentHz = dbList[i]
            currentDb = 30
            cantHear = false
            firstTouch = true
            whileState = true
            dbSet = mutableSetOf<Int>()
            //측정 헤르츠의 데시벨 측정 시작
            while (whileState){

                startMp(mediaPlayer)

            } // 특정 헤르츠 측정 끝

        } // End of first test For


        // start of second test
        for(i in testAgainList()){

            // 해당 헤르츠의 음원 준비
            val mediaPlayer = MediaPlayer.create(context, resIdList2[i])

            //해당 헤르츠 테스트 초기 세팅
            currentHz = dbList2[i]
            currentDb = 30
            cantHear = false
            firstTouch = true
            whileState = true
            dbSet = mutableSetOf<Int>()

            //측정 헤르츠의 데시벨 측정 시작
            while (whileState){

                startMp(mediaPlayer)

            } // 특정 헤르츠 측정 끝

        }

        // 왼쪽 제 실행
        if(currentDirec==1){
            currentDirec=0
            doTest()
        }
        // 모든 테스트 종료
        if(currentDirec==0){
            Log.d("tag","fin")

            //findNavController().navigate(R.id.action_prepareFragment_to_onBoardingFirstFragment)
        }
    }


    // 특정 데시벨 테스트
    private fun startMp( mediaPlayer: MediaPlayer){
        if(currentDb<0){
            result[currentDirec]!![currentHz] = 0
            whileState = false
            return
        }else if(currentDb>100){
            result[currentDirec]!![currentHz] = 100
            whileState = false
            return
        }

        var nowHear = false
        var button: Button? = null


        // 방향 설정
        when (currentDirec){
            1-> {   //right
                leftDb = 0f
                rightDb = dbMap[currentDb]!!
                buttonLeft.setOnClickListener {}
                button = buttonRight
            }
            0-> {   //left
                leftDb = dbMap[currentDb]!!
                rightDb = 0f
                buttonRight.setOnClickListener {}
                button = buttonLeft
            }
        }

        // 볼륨 세팅
        mediaPlayer.setVolume(leftDb,rightDb)

        // job 스레드 실행
        val job = GlobalScope.launch(Dispatchers.Default){
            setImageGone()
            setCountVisible()

            textCount.text = "3"
            sleep(1000)
            textCount.text = "2"
            sleep(1000)
            textCount.text = "1"
            sleep(1000)

            setCountGone()
            setImageVisible()
            mediaPlayer.start()
            // 버틍 기능 시작
            button!!.setOnClickListener {
                Toast.makeText(context,"activate",Toast.LENGTH_SHORT).show()
                nowHear = true
            }
            sleep(3000)
            //버튼 기능 끝
            button!!.setOnClickListener {}

            // 첫 데시벨부터 들리지 않을 때 오름차순 검사 시작
            if(firstTouch && !nowHear) { cantHear = true }
            firstTouch = false

            // 들었는지 못들었는지 판단
            when(cantHear){
                true->{ // 오름 차순 일 때
                    when(nowHear){
                        true-> { // 오름차순 중에 들었을 때
                            result[currentDirec]!![currentHz] = currentDb   // 데시벨 픽스
                            whileState = false
                        }
                        false-> currentDb+=10   // 오름차순 중 못 들었을 때
                    }
                }
                false->{ // 내림차순인 상황
                    when(nowHear){
                        true -> currentDb -= 10 // 내림차순 중 들었을 때
                        false ->{               // 내림차순 중 못 들었을 때
                            if(dbSet.contains(currentDb)){
                                result[currentDirec]!![currentHz] = currentDb   //데시벨 픽스
                                whileState = false
                            }
                            else{
                                dbSet.add(currentDb)
                                currentDb+=5
                            }
                        }
                    }
                }
            }
            //test//////////////////////////////////////////////////////////
            Log.d("tag","${dbSet.size} db Size")

            //다시 기본값 설정
            nowHear = false
        }

        // job 스레드가 끝나면 실행
        // 지금 startMp() 함수가 끝날때 까지 새로운 startMp()실행 방지
        runBlocking {
            job.join()
        }
    }

    // 재검사가 필요한 목록을 리스트로 반환
    private fun testAgainList():ArrayList<Int>{
        val list = arrayListOf<Int>()
        // 1000~8000 사이를 검사
        for(i in 0..2){
            if(result[currentDirec]?.get(dbList[i])
                    ?.minus(result[currentDirec]?.get(dbList[i + 1])!!)?.let { abs(it) }!! >=20) list.add(i+1)
        }
        //500~700검사
        if(result[currentDirec]?.get(1000)?.let { it -> result[currentDirec]?.get(500)?.minus(it)?.let { abs(it) } }!! >=20) list.add(0)
        return list
    }


    // image setting function
    private fun setImageVisible(){
        viewModel.currentImageVisible.postValue(View.VISIBLE)
    }

    private fun setImageGone(){
        viewModel.currentImageVisible.postValue(View.GONE)
    }

    private fun setCountVisible(){
        viewModel.currentCountVisible.postValue(View.VISIBLE)
    }

    private fun setCountGone(){
        viewModel.currentCountVisible.postValue(View.GONE)
    }
}