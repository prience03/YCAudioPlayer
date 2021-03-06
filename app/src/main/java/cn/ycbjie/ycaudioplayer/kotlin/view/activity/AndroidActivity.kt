package cn.ycbjie.ycaudioplayer.kotlin.view.activity

import android.graphics.Typeface
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import cn.ycbjie.ycaudioplayer.R
import cn.ycbjie.ycaudioplayer.base.view.BaseActivity
import cn.ycbjie.ycaudioplayer.base.view.BasePagerAdapter
import cn.ycbjie.ycaudioplayer.kotlin.presenter.AndroidPresenter
import cn.ycbjie.ycaudioplayer.kotlin.view.fragment.AndroidHomeFragment
import cn.ycbjie.ycaudioplayer.kotlin.view.fragment.AndroidKnowledgeFragment
import cn.ycbjie.ycaudioplayer.kotlin.view.fragment.AndroidProfileFragment
import cn.ycbjie.ycaudioplayer.kotlin.view.fragment.AndroidProjectFragment
import cn.ycbjie.ycaudioplayer.model.TabEntity
import cn.ycbjie.ycaudioplayer.utils.logger.AppLogUtils
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar
import com.flyco.tablayout.CommonTabLayout
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import java.lang.ref.WeakReference
import java.util.ArrayList

/**
 * <pre>
 *     @author 杨充
 *     blog  :
 *     time  : 2017/05/30
 *     desc  : kotlin学习
 *     revise:
 * </pre>
 */
class AndroidActivity : BaseActivity<AndroidPresenter>(){

    var presenter: AndroidPresenter? = null

    /**
     * 定义局部变量和常量
     * 0. Kotlin声明变量与Java声明变量有些不一样，Java变量类型在前，变量名在后，
     *   而Kotlin则相反，变量名在前，变量类型在后，中间加:(冒号)
     *   并且Kotlin可以自动判断变量的类型。
     *
     * 1.常量
     *      常量使用val关键字
     *
     * 2.变量
     *      变量使用var关键字
     */
    private var mTvTitleLeft :TextView?=null
    private var mLlTitleMenu :FrameLayout?=null
    private var mToolbarTitle :TextView?=null
    private var mIvRightImg :ImageView?=null
    private var viewPager: ViewPager? = null
    private var ctlTable : CommonTabLayout? =null
    private var fragments = mutableListOf<Fragment>()
    private var pageAdapter : BasePagerAdapter? = null
    private var index: Int = 0

    /**
     * 定义函数
     * 1.与Java定义函数的区别在于：Kotlin在定义函数的时候要加个fun关键词，函数的返回值前后不同，Java的在前面，kotlin在后面
     * 2.如果一个函数只有一个并且是表达式函数体并且是返回类型自动推断的话，可以直接这样写
     *   fun getResult(a: Int, b: Int) = a + b
     * 3.如果函数返回一个无意义的值，相当于Java的void，则可以这样写
     *   fun initView(){}
     */
    override fun getContentView(): Int {
        return R.layout.activity_wan_android
    }

    override fun initView() {
        mTvTitleLeft = findViewById(R.id.tv_title_left) as TextView?
        mLlTitleMenu = findViewById(R.id.ll_title_menu) as FrameLayout?
        mToolbarTitle = findViewById(R.id.toolbar_title) as TextView?
        mIvRightImg = findViewById(R.id.iv_right_img) as ImageView?
        viewPager = findViewById(R.id.vp_pager) as ViewPager?
        ctlTable = findViewById(R.id.ctl_table) as CommonTabLayout?

        mTvTitleLeft!!.visibility = View.VISIBLE
        mTvTitleLeft!!.textSize = 16.0f
        mTvTitleLeft!!.typeface = Typeface.DEFAULT
        mLlTitleMenu!!.visibility = View.GONE
        mIvRightImg!!.visibility = View.VISIBLE
        YCAppBar.setStatusBarColor(this, ContextCompat.getColor(this, R.color.redTab))
        initFragment()
        initTabLayout()
    }


    override fun initListener() {

    }


    override fun initData() {

    }

    private fun initFragment() {
        fragments.add(AndroidHomeFragment())
        fragments.add(AndroidKnowledgeFragment())
        fragments.add(AndroidProjectFragment())
        fragments.add(AndroidProfileFragment())

        pageAdapter = BasePagerAdapter(supportFragmentManager, fragments)
        viewPager.run {
            this!!.adapter = pageAdapter
            addOnPageChangeListener(PagerChangeListener(this@AndroidActivity))
            offscreenPageLimit = fragments.size
        }
    }

    private fun initTabLayout() {
        val mTabEntities = ArrayList<CustomTabEntity>()
        val mIconUnSelectIds = this.resources.obtainTypedArray(R.array.main_tab_un_select)
        val mIconSelectIds = this.resources.obtainTypedArray(R.array.main_tab_select)
        val mainTitles = this.resources.getStringArray(R.array.main_title)
        for (i in mainTitles.indices) {
            val unSelectId = mIconUnSelectIds.getResourceId(i, R.drawable.ic_tab_main_art_uncheck)
            val selectId = mIconSelectIds.getResourceId(i, R.drawable.ic_tab_main_art_checked)
            mTabEntities.add(TabEntity(mainTitles[i], selectId, unSelectId))
        }
        mIconUnSelectIds.recycle()
        mIconSelectIds.recycle()

        /**
         * 此处for仅供学习使用
         * 使用for循环
         * in操作符可以判断是否a是否在mTabEntities里面
         */
        for (a in mTabEntities){
            AppLogUtils.e("使用for循环"+ a.tabTitle)
        }

        ctlTable?.setTabData(mTabEntities)
        ctlTable?.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                if(position>=0 && position<fragments.size){
                    viewPager!!.currentItem = position
                }
            }

            override fun onTabReselect(position: Int) {

            }
        })
    }

    class PagerChangeListener(activity: AndroidActivity) : ViewPager.OnPageChangeListener {
        private var weakActivity: WeakReference<AndroidActivity>? = null
        init {
            this.weakActivity = WeakReference(activity)
        }

        override fun onPageScrollStateChanged(state: Int) {}

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            var activity: AndroidActivity? = weakActivity?.get()
            if (activity != null) {
                activity.index = position
                activity.selectByIndex(position)
            }
        }
    }

    /**
     * 使用when表达式
     * when表达式就相当于Java的switch表达式，省去了case和break，并且支持各种类型。
     */
    private fun selectByIndex(position: Int) {
        when (position){
            0 ->{
                ctlTable!!.currentTab = 0
                mTvTitleLeft!!.text = "首页"
            }
            1 ->{
                ctlTable!!.currentTab = 1
                mTvTitleLeft!!.text = "项目"
            }
            2 ->{
                ctlTable!!.currentTab = 2
                mTvTitleLeft!!.text = "博客"
            }
            3 ->{
                ctlTable!!.currentTab = 3
                mTvTitleLeft!!.text = "我的"
            }
        }
    }


}
