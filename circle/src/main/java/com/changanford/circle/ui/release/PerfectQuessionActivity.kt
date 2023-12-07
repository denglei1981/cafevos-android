package com.changanford.circle.ui.release

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.PerfectquessionActivityBinding
import com.changanford.circle.ui.release.utils.ParamsUtils
import com.changanford.circle.ui.release.widget.OpenCarcme
import com.changanford.circle.ui.release.widget.OpenCarcme.OpenCarcmeCallBack
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.QueryInfo.QuessionBean
import com.changanford.common.bean.QueryInfo.QuessionBean.OptionBean
import com.changanford.common.bean.STSBean
import com.changanford.common.net.CommonResponse
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.AliYunOssUploadOrDownFileConfig
import com.changanford.common.util.AliYunOssUploadOrDownFileConfig.OnUploadFile
import com.changanford.common.util.AppUtils
import com.changanford.common.util.PictureUtils.openGarlly
import com.changanford.common.util.PictureUtils.opencarcme
import com.changanford.common.utilext.GlideUtils.handleImgUrl
import com.changanford.common.utilext.GlideUtils.loadRoundLocal
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.tools.DoubleUtils
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * 单选多选完善问题
 */
@Route(path = "/circle/PerfectQuessionActivity")
class PerfectQuessionActivity() :
    BaseActivity<PerfectquessionActivityBinding, PerfectQuessionActivityViewModule>() {
    var prefectQuessionDragAdapter: PrefectQuessionDragAdapter? = null
    private lateinit var HeadView: View
    lateinit var ettimu: EditText
    lateinit var ivshanchu: ImageView
    private lateinit var iv_timuimg: ImageView
    private lateinit var list: ArrayList<OptionBean>
    var quessionBean: QuessionBean? = QuessionBean()
    var optionBeans: List<OptionBean> = ArrayList()
    var quessionType //0单选，1多选
            = 0
    private lateinit var openCarcme: OpenCarcme
    private lateinit var upimgs: ArrayList<String>
    var position = 0
    var queryId = 0
    override fun initView() {
        AppUtils.setStatusBarHeight(binding!!.title.barTitleView, this)
        if (intent.getSerializableExtra("quessionBean") != null) {
            queryId = intent.getIntExtra("queryId", 0)
            position = intent.getIntExtra("position", 0)
            quessionBean = intent.getSerializableExtra("quessionBean") as QuessionBean?
        } else {
            quessionType = intent.getIntExtra("quessionType", 0)
            quessionBean!!.optionList = optionBeans
            quessionBean!!.questionType = quessionType
            quessionBean!!.isQuestionNecessary = 0
        }
        binding!!.addchoice.mcb.setOnClickListener({ v: View? ->  //是否必填
            if (binding!!.addchoice.mcb.isChecked()) {
                quessionBean!!.setIsQuestionNecessary(0)
            } else {
                quessionBean!!.setIsQuestionNecessary(1)
            }
        })
        binding!!.addchoice.multeorsignle.setOnClickListener({ v: View? ->  //切换单选多选
            if (quessionType == 0) { //是单选，点击切换成多选
                quessionType = 1
            } else { //是多选，点击切换成单选
                quessionType = 0
            }
            quessionBean!!.setQuestionType(quessionType)
        })
        binding!!.title.barImgBack.setOnClickListener(View.OnClickListener { finish() })
        if (quessionType == 0) {
            binding!!.title.barTvTitle.text = "单选题"
            binding!!.addchoice.multeorsignletxt.text = "切换至多选题"
        } else {
            binding!!.title.barTvTitle.text = "多选题"
            binding!!.addchoice.multeorsignletxt.text = "切换至单选题"
        }
        prefectQuessionDragAdapter = PrefectQuessionDragAdapter()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding!!.prefectquessrec.layoutManager = layoutManager
        prefectQuessionDragAdapter!!.draggableModule.isSwipeEnabled = false
        prefectQuessionDragAdapter!!.draggableModule.isDragEnabled = true
        prefectQuessionDragAdapter!!.draggableModule.setOnItemDragListener(onItemDragListener)
        prefectQuessionDragAdapter!!.draggableModule.setOnItemSwipeListener(onItemSwipeListener)
        prefectQuessionDragAdapter!!.draggableModule.itemTouchHelperCallback.setSwipeMoveFlags(
            ItemTouchHelper.START or ItemTouchHelper.END
        )
        HeadView = LayoutInflater.from(this).inflate(R.layout.prefectquessionrechead, null, false)
        prefectQuessionDragAdapter!!.addHeaderView(HeadView)
        binding!!.prefectquessrec.adapter = prefectQuessionDragAdapter
        if (intent.getSerializableExtra("quessionBean") == null) {
            list = ArrayList()
            list!!.add(OptionBean(""))
            list!!.add(OptionBean(""))
            quessionBean!!.optionList.addAll(list!!)
            prefectQuessionDragAdapter!!.addData(list!!)
        }
        ettimu = HeadView.findViewById(R.id.et_timu)
        ivshanchu = HeadView.findViewById(R.id.iv_shanchu)
        val tvnum = HeadView.findViewById<TextView>(R.id.tv_num)
        iv_timuimg = HeadView.findViewById(R.id.iv_timuimg)
        prefectQuessionDragAdapter!!.setOnItemChildClickListener(object : OnItemChildClickListener {
            override fun onItemChildClick(
                adapter: BaseQuickAdapter<*, *>,
                view: View,
                position: Int
            ) {
                if (view.id == R.id.iv_img) {
                    openxiangche(position)
                } else if (view.id == R.id.iv_delete) {
                    if (prefectQuessionDragAdapter!!.data.size == 2) {
                        return
                    }
                    prefectQuessionDragAdapter!!.remove(position)
                    quessionBean!!.optionList.removeAt(position)
                } else if (view.id == R.id.iv_del) {
                    prefectQuessionDragAdapter!!.getItem(position).bdoptionImgUrl = ""
                    prefectQuessionDragAdapter!!.getItem(position).optionImgUrl = ""
                    prefectQuessionDragAdapter!!.notifyDataSetChanged()
                }
            }
        })
        ivshanchu.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                quessionBean!!.questionImgUrl = ""
                quessionBean!!.bDquestionImgUrl = ""
                iv_timuimg.setImageResource(R.mipmap.add_image)
                ivshanchu.setVisibility(View.INVISIBLE)
            }
        })
        ettimu.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                tvnum.text = s.length.toString() + "/20"
            }
        })
        iv_timuimg.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                openxiangche()
                hideInput()
            }
        })
        binding!!.commit.setOnClickListener({ v: View? ->
            quessionBean!!.setQuestionInfo(ettimu.getText().toString().trim { it <= ' ' })
            if (quessionCanBack() && !DoubleUtils.isFastDoubleClick()) {
                if (!TextUtils.isEmpty(quessionBean!!.getQuestionImgUrl()) || quessionBean!!.getOptionList().size > 0) {
                    var isnext: Boolean = false
                    for (i in quessionBean!!.getOptionList().indices) {
                        if (!TextUtils.isEmpty(
                                quessionBean!!.getOptionList().get(i).getBdoptionImgUrl()
                            )
                        ) {
                            isnext = true
                        }
                    }
                    if (!TextUtils.isEmpty(quessionBean!!.getBDquestionImgUrl())) {
                        isnext = true
                    }
                    if (isnext) {
                        val dialog: LoadDialog = LoadDialog(this@PerfectQuessionActivity)
                        dialog.setCancelable(false)
                        dialog.setCanceledOnTouchOutside(false)
                        dialog.setLoadingText("图片上传中..")
                        dialog.show()
                        upImg(dialog)
                    } else {
                        val intent: Intent = Intent()
                        val bundle: Bundle = Bundle()
                        bundle.putSerializable("quession", quessionBean)
                        bundle.putInt("position", position)
                        intent.putExtra("mbund", bundle)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                } else {
                    val intent: Intent = Intent()
                    val bundle: Bundle = Bundle()
                    bundle.putSerializable("quession", quessionBean)
                    bundle.putInt("position", position)
                    intent.putExtra("mbund", bundle)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        })
        binding!!.llAdd.setOnClickListener({ v: View? ->
            val optionBean: OptionBean = OptionBean("")
            prefectQuessionDragAdapter!!.addData(optionBean)
            quessionBean!!.getOptionList().add(optionBean)
        })
    }

    //上传图片
    private fun upImg(dialog: LoadDialog) {
        viewModel!!.GetOSS { response: CommonResponse<STSBean> ->
            if (response.code == 0) {
                val stsBean: STSBean? = response.data
                val imglist: MutableList<String> = ArrayList()
                if (!TextUtils.isEmpty(quessionBean!!.getBDquestionImgUrl())) {
                    imglist.add(quessionBean!!.getBDquestionImgUrl())
                }
                if (quessionBean!!.getOptionList().size > 0) {
                    for (i in quessionBean!!.getOptionList().indices) {
                        if (!TextUtils.isEmpty(
                                quessionBean!!.getOptionList().get(i).getBdoptionImgUrl()
                            )
                        ) {
                            imglist.add(quessionBean!!.getOptionList().get(i).getBdoptionImgUrl())
                        } else {
                            imglist.add("")
                        }
                    }
                }
                uploadImgs(imglist, stsBean, 0, dialog)
            } else {
                runOnUiThread(object : Runnable {
                    override fun run() {
                        dialog.dismiss()
                    }
                })
            }
            null
        }
    }

    private fun uploadImgs(
        upfiles: MutableList<String>,
        stsBean: STSBean?,
        count: Int,
        dialog: LoadDialog
    ) {
        val size = upfiles.size
        if (upfiles[count].isEmpty()) {
            val num = count + 1
            if (num == size) {
                dialog.dismiss()
                Log.d("上传之后的图片集合", JSON.toJSONString(upfiles))
                if (!TextUtils.isEmpty(quessionBean!!.bDquestionImgUrl)) {
                    quessionBean!!.questionImgUrl = upfiles.get(0)
                    upfiles.removeAt(0)
                }
                for (i in quessionBean!!.optionList.indices) {
                    if (!TextUtils.isEmpty(quessionBean!!.optionList[i].bdoptionImgUrl)) {
                        quessionBean!!.optionList.get(i).optionImgUrl = upfiles.get(i)
                    }
                }
                val intent = Intent()
                val bundle = Bundle()
                bundle.putSerializable("quession", quessionBean)
                bundle.putInt("position", position)
                intent.putExtra("mbund", bundle)
                setResult(RESULT_OK, intent)
                finish()
                return
            }
            uploadImgs(upfiles, stsBean, num, dialog)
            return
        }
        AliYunOssUploadOrDownFileConfig.getInstance(this).initOss(
            stsBean!!.endpoint, stsBean.accessKeyId,
            stsBean.accessKeySecret, stsBean.securityToken
        )
        val type =
            upfiles[count].substring(upfiles[count].lastIndexOf(".") + 1, upfiles[count].length)
        val path = stsBean.tempFilePath + System.currentTimeMillis() + "." + type
        //        String uploadFilePath = stsBean.getCdn() + path;
        AliYunOssUploadOrDownFileConfig.getInstance(this)
            .uploadFile(stsBean.bucketName, path, upfiles[count], "", 0)
        upfiles[count] = path
        AliYunOssUploadOrDownFileConfig.getInstance(this).setOnUploadFile(object : OnUploadFile {
            override fun onUploadFileSuccess(info: String) {
                val scount = count + 1
                if (scount == size) {
                    dialog.dismiss()
                    Log.d("上传之后的图片集合", JSON.toJSONString(upfiles))
                    if (!TextUtils.isEmpty(quessionBean!!.bDquestionImgUrl)) {
                        quessionBean!!.questionImgUrl = upfiles.get(0)
                        upfiles.removeAt(0)
                    }
                    for (i in quessionBean!!.optionList.indices) {
                        if (!TextUtils.isEmpty(quessionBean!!.optionList[i].bdoptionImgUrl)) {
                            quessionBean!!.optionList.get(i).optionImgUrl = upfiles.get(i)
                        }
                    }
                    val intent = Intent()
                    val bundle = Bundle()
                    bundle.putSerializable("quession", quessionBean)
                    bundle.putInt("position", position)
                    intent.putExtra("mbund", bundle)
                    setResult(RESULT_OK, intent)
                    finish()
                    return
                }
                uploadImgs(upfiles, stsBean, scount, dialog)
            }

            override fun onUploadFileFailed(errCode: String) {
                dialog.dismiss()
            }

            override fun onuploadFileprogress(
                request: PutObjectRequest,
                currentSize: Long,
                totalSize: Long
            ) {
                runOnUiThread(object : Runnable {
                    override fun run() {
//                        progressDialog.setProgress((int) ((currentSize*100/totalSize)));
                    }
                })
            }
        })
    }

    private fun quessionCanBack(): Boolean {
        return ParamsUtils.isquessionOk(quessionBean)
    }

    override fun initData() {
        if (queryId == 0) { //添加调查
            if (intent.getSerializableExtra("quessionBean") != null) {
                ettimu!!.setText(quessionBean!!.questionInfo)
            }
            if (!TextUtils.isEmpty(quessionBean!!.bDquestionImgUrl)) {
                loadRoundLocal(quessionBean!!.bDquestionImgUrl, (iv_timuimg)!!, 5f)
            }
            prefectQuessionDragAdapter!!.setList(quessionBean!!.optionList)
        } else {  //修改调查
            if (!TextUtils.isEmpty(quessionBean!!.questionImgUrl)) {
                downloadFM(handleImgUrl(quessionBean!!.questionImgUrl))
            }
            ettimu!!.setText(quessionBean!!.questionInfo)
            if (quessionBean!!.optionList.size > 0) {
                var isdownload = false
                for (i in quessionBean!!.optionList.indices) {
                    val optionBean = quessionBean!!.optionList[i]
                    if (!TextUtils.isEmpty(optionBean.optionImgUrl)) {
                        isdownload = true //需要下载图片
                    }
                }
                if (isdownload) {
                    downloadListIMG(
                        quessionBean!!.optionList,
                        0,
                        quessionBean!!.optionList.size
                    ) //下载选项中的图片
                } else {
                    prefectQuessionDragAdapter!!.setList(quessionBean!!.optionList)
                }
            }
        }

//        if (getIntent().getSerializableExtra("quessionBean")!=null){
//            ettimu.setText(quessionBean.getQuestionInfo());
//            if (!TextUtils.isEmpty(quessionBean.getBDquestionImgUrl())){
//                GlideUtils.INSTANCE.loadRoundLocal(quessionBean.getBDquestionImgUrl(), iv_timuimg,5);
//                prefectQuessionDragAdapter.setList(quessionBean.getOptionList());
//            }else{
//
//                if (!TextUtils.isEmpty(quessionBean.getQuestionImgUrl())){
//                    downloadFM(GlideUtils.INSTANCE.handleImgUrl(quessionBean.getQuestionImgUrl()));
//                }
//                if (quessionBean.getOptionList().size()>0){
//                    boolean isdownload =false;
//                    for (int i = 0; i <quessionBean.getOptionList().size() ; i++) {
//                        QueryInfo.QuessionBean.OptionBean optionBean =quessionBean.getOptionList().get(i);
//                        if (!TextUtils.isEmpty(optionBean.getOptionImgUrl())){
//                            isdownload = true; //需要下载图片
//                        }
//                    }
//                    if (isdownload){
//                        downloadListIMG(quessionBean.getOptionList(),0,quessionBean.getOptionList().size()); //下载选项中的图片
//                    }else{
//
//                    }
//
//
//                }
//            }
//        }
    }

    /**
     * 下载图片集合
     *
     * @param
     */
    fun downloadListIMG(urls: List<OptionBean>, count: Int, total: Int) {
        if (!TextUtils.isEmpty(urls[count].optionImgUrl)) {
            Observable.create(object : ObservableOnSubscribe<File?> {
                @Throws(Exception::class)
                override fun subscribe(e: ObservableEmitter<File?>) {
                    //通过gilde下载得到file文件,这里需要注意android.permission.INTERNET权限
                    e.onNext(
                        Glide.with(this@PerfectQuessionActivity)
                            .load(handleImgUrl(urls[count].optionImgUrl))
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get()
                    )
                    e.onComplete()
                }
            }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(object : Consumer<File?> {
                    @Throws(Exception::class)
                    override fun accept(file: File?) {
                        //获取到下载得到的图片，进行本地保存
                        val pictureFolder = Environment.getExternalStorageDirectory()
                        //第二个参数为你想要保存的目录名称
                        val appDir = File(pictureFolder, "Uni")
                        if (!appDir.exists()) {
                            appDir.mkdirs()
                        }
                        val fileName = System.currentTimeMillis().toString() + ".jpg"
                        val destFile = File(appDir, fileName)
                        //把gilde下载得到图片复制到定义好的目录中去
                        copy(file, destFile)
                        //                            quessionBean.getOptionList().get(count).setOptionImgUrl(destFile.getPath());
                        quessionBean!!.optionList.get(count).bdoptionImgUrl = destFile.path
                        // 最后通知图库更新
//                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                                    Uri.fromFile(new File(destFile.getPath()))));
                        val scount = count + 1
                        if (scount == total) {
                            runOnUiThread(object : Runnable {
                                override fun run() {
                                    prefectQuessionDragAdapter!!.setList(quessionBean!!.optionList)
                                }
                            })
                            return
                        }
                        downloadListIMG(urls, scount, urls.size)
                    }
                })
        } else {
            val scount = count + 1
            if (scount == total) {
                runOnUiThread(object : Runnable {
                    override fun run() {
                        prefectQuessionDragAdapter!!.setList(quessionBean!!.optionList)
                    }
                })
                return
            }
            downloadListIMG(urls, scount, urls.size)
        }
    }

    fun downloadFM(url: String?) {
        Observable.create(object : ObservableOnSubscribe<File?> {
            @Throws(Exception::class)
            override fun subscribe(e: ObservableEmitter<File?>) {
                //通过gilde下载得到file文件,这里需要注意android.permission.INTERNET权限
                e.onNext(
                    Glide.with(this@PerfectQuessionActivity)
                        .load(url)
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get()
                )
                e.onComplete()
            }
        }).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .subscribe(object : Consumer<File?> {
                @Throws(Exception::class)
                override fun accept(file: File?) {

                    //获取到下载得到的图片，进行本地保存
                    val pictureFolder = Environment.getExternalStorageDirectory()
                    //第二个参数为你想要保存的目录名称
                    val appDir = File(pictureFolder, "Uni")
                    if (!appDir.exists()) {
                        appDir.mkdirs()
                    }
                    val fileName = System.currentTimeMillis().toString() + ".jpg"
                    val destFile = File(appDir, fileName)
                    //把gilde下载得到图片复制到定义好的目录中去
                    copy(file, destFile)
                    //                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                                Uri.fromFile(new File(destFile.getPath()))));
                    runOnUiThread(object : Runnable {
                        override fun run() {
                            loadRoundLocal(handleImgUrl(url), (iv_timuimg)!!, 5f)
                            //                                quessionBean.setQuestionImgUrl(destFile.getPath());
                            quessionBean!!.bDquestionImgUrl = destFile.path
                        }
                    })
                }
            })
    }

    /**
     * 复制文件
     *
     * @param source 输入文件
     * @param target 输出文件
     */
    fun copy(source: File?, target: File?) {
        var fileInputStream: FileInputStream? = null
        var fileOutputStream: FileOutputStream? = null
        try {
            fileInputStream = FileInputStream(source)
            fileOutputStream = FileOutputStream(target)
            val buffer = ByteArray(1024)
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fileInputStream!!.close()
                fileOutputStream!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    var onItemDragListener: OnItemDragListener = object : OnItemDragListener {
        override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder, pos: Int) {
//            Log.d(TAG, "drag start");
//            final BaseViewHolder holder = ((BaseViewHolder) viewHolder);
//
//            // 开始时，item背景色变化，demo这里使用了一个动画渐变，使得自然
//            int startColor = Color.WHITE;
//            int endColor = Color.rgb(245, 245, 245);
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                ValueAnimator v = ValueAnimator.ofArgb(startColor, endColor);
//                v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        holder.itemView.setBackgroundColor((int)animation.getAnimatedValue());
//                    }
//                });
//                v.setDuration(300);
//                v.start();
//            }
        }

        override fun onItemDragMoving(
            source: RecyclerView.ViewHolder,
            from: Int,
            target: RecyclerView.ViewHolder,
            to: Int
        ) {
        }

        override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder, pos: Int) {
//            Log.d(TAG, "drag end");
//            final BaseViewHolder holder = ((BaseViewHolder) viewHolder);
//            // 结束时，item背景色变化，demo这里使用了一个动画渐变，使得自然
//            int startColor = Color.rgb(245, 245, 245);
//            int endColor = Color.WHITE;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                ValueAnimator v = ValueAnimator.ofArgb(startColor, endColor);
//                v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        holder.itemView.setBackgroundColor((int)animation.getAnimatedValue());
//                    }
//                });
//                v.setDuration(300);
//                v.start();
//            }
        }
    }

    // 侧滑监听
    var onItemSwipeListener: OnItemSwipeListener = object : OnItemSwipeListener {
        override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder, pos: Int) {
            Log.d(TAG, "view swiped start: $pos")
            val holder = (viewHolder as BaseViewHolder)
        }

        override fun clearView(viewHolder: RecyclerView.ViewHolder, pos: Int) {
            Log.d(TAG, "View reset: $pos")
            val holder = (viewHolder as BaseViewHolder)
        }

        override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder, pos: Int) {
            Log.d(TAG, "View Swiped: $pos")
        }

        override fun onItemSwipeMoving(
            canvas: Canvas,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            isCurrentlyActive: Boolean
        ) {
            canvas.drawColor(ContextCompat.getColor(this@PerfectQuessionActivity, R.color.white))
        }
    }

    /**
     * 直接打开相册
     */
    private fun openxiangche() {
        if (!DoubleUtils.isFastDoubleClick()) {
            openCarcme = OpenCarcme(this@PerfectQuessionActivity, object : OpenCarcmeCallBack {
                override fun onPicBack() {
                    openGarlly(
                        this@PerfectQuessionActivity,
                        1,
                        object : OnResultCallbackListener<LocalMedia?> {
                            override fun onResult(result: List<LocalMedia?>) {
                                for (media: LocalMedia? in result) {
                                    quessionBean!!.questionImgUrl = ""
                                    quessionBean!!.bDquestionImgUrl = AppUtils.getFinallyPath(media)
                                    loadRoundLocal(
                                        AppUtils.getFinallyPath(media),
                                        (iv_timuimg)!!,
                                        5f
                                    )
                                    ivshanchu!!.visibility = View.VISIBLE
                                }
                            }

                            override fun onCancel() {}
                        },
                        2,
                        1
                    )
                }

                override fun onCarcme() {
                    opencarcme(
                        this@PerfectQuessionActivity,
                        object : OnResultCallbackListener<LocalMedia?> {
                            override fun onResult(result: List<LocalMedia?>) {
                                // 结果回调
                                if (result.size > 0) {
                                    result?.forEach { media->
                                        media?.let {
                                            var path: String? = ""
                                            if (media.isCut && !media.isCompressed) {
                                                // 裁剪过
                                                path = media.cutPath
                                            } else if (media.isCompressed || (media.isCut && media.isCompressed)) {
                                                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                                                path = media.compressPath
                                            } else {
                                                // 原图
                                                path = media.path
                                            }
                                            quessionBean!!.questionImgUrl = ""
                                            quessionBean!!.bDquestionImgUrl =
                                                AppUtils.getFinallyPath(media)
                                            loadRoundLocal(
                                                AppUtils.getFinallyPath(media),
                                                (iv_timuimg)!!,
                                                5f
                                            )
                                        }
                                    }
                                }
                            }

                            override fun onCancel() {
                                // 取消
                            }
                        })
                }
            })
            openCarcme!!.showPopupWindow()
        }
    }

    /**
     * 直接打开相册
     */
    private fun openxiangche(index: Int) {

        // 进入相册 以下是例子：不需要的api可以不写
        openGarlly(this@PerfectQuessionActivity, object : OnResultCallbackListener<LocalMedia?> {
            override fun onResult(result: List<LocalMedia?>) {
                for (media: LocalMedia? in result) {
                    quessionBean!!.optionList.get(index).optionImgUrl = ""
                    quessionBean!!.optionList.get(index).bdoptionImgUrl =
                        AppUtils.getFinallyPath(media)
                    prefectQuessionDragAdapter!!.getItem(index).optionImgUrl = ""
                    prefectQuessionDragAdapter!!.getItem(index).bdoptionImgUrl =
                        AppUtils.getFinallyPath(media)
                    prefectQuessionDragAdapter!!.setData(
                        index,
                        prefectQuessionDragAdapter!!.getItem(index)
                    )
                }
            }

            override fun onCancel() {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        //        try{
//            FileUtils.deleteAllInDir( Environment.getExternalStorageDirectory().getPath()+"/Uni");
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                    Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()+"/Uni"))));
//        }catch (Exception e){
//
//        }
    }

    protected fun hideInput() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val v = window.peekDecorView()
        if (null != v) {
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    companion object {
        private val TAG = "PerfectQuessionActivity"
    }
}