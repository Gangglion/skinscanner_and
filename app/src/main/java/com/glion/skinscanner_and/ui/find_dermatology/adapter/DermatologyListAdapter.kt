package com.glion.skinscanner_and.ui.find_dermatology.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.util.LogUtil
import com.glion.skinscanner_and.databinding.ItemDermatologyListBinding
import com.glion.skinscanner_and.ui.find_dermatology.data.DermatologyData
import com.kakao.vectormap.GestureType
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTransition
import com.kakao.vectormap.label.Transition
import com.kakao.vectormap.mapwidget.InfoWindowOptions
import com.kakao.vectormap.mapwidget.component.GuiImage
import com.kakao.vectormap.mapwidget.component.GuiLayout
import com.kakao.vectormap.mapwidget.component.GuiText
import com.kakao.vectormap.mapwidget.component.Orientation

class DermatologyListAdapter(
    private val mContext: Context,
    private val itemList: MutableList<DermatologyData>
) : RecyclerView.Adapter<DermatologyListAdapter.ViewHolder>() {
    companion object {
        const val LABEL_ID = "iconLabel"
        const val INFO_WINDOW_ID = "simpleLayout"
    }

    inner class ViewHolder(private val binding: ItemDermatologyListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DermatologyData) {
            with(binding) {
                tvItemTitle.text = item.dermatologyTitle
                tvItemAddr.text = item.dermatologyAddr
                tvItemDist.text = mContext.getString(R.string.format_dist).format(item.dermatologyDist.toFloat() / 1000)
                tvItemNumber.apply {
                    text = item.dermatologyNumber
                    setOnClickListener {
                        clickPhone(text.toString())
                    }
                }
                tvItemUrl
                tvItemUrl.apply {
                    text = item.dermatologyUrl
                    setOnClickListener {
                        clickWeb(text.toString())
                    }
                }
                setMap(mvItem, item)
            }
        }

        private fun clickPhone(number: String) {
            val intentDial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            mContext.startActivity(intentDial)
        }

        private fun clickWeb(url: String) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            mContext.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDermatologyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    /**
     * 주변 피부과 데이터 업데이트
     * @param [newItemList] 추가되는 아이템 리스트
     */
    fun updateData(newItemList: List<DermatologyData>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = itemList.size

            override fun getNewListSize(): Int = newItemList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return (itemList[oldItemPosition].dermatologyLat == newItemList[newItemPosition].dermatologyLat) &&
                        (itemList[oldItemPosition].dermatologyLng == newItemList[newItemPosition].dermatologyLng)
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return itemList[oldItemPosition] == newItemList[newItemPosition]
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        itemList.clear()
        itemList.addAll(newItemList)
        diffResult.dispatchUpdatesTo(this)
    }

    /**
     * 장소 카카오맵 세팅
     */
    private fun setMap(map: MapView, item: DermatologyData) {
        map.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {
                    // note : 지도 API 가 정상적으로 종료될 떄 호출됨
                }

                override fun onMapError(e: Exception?) {
                    // 인증 실패 및 지도 사용 중 에러 발생 시 호출 됨
                    LogUtil.e("인증 실패 및 지도 사용 중 에러 발생", e)
                }
            },
            object : KakaoMapReadyCallback() {
                override fun onMapReady(kakaoMap: KakaoMap) {
                    // note : 인증 API 가 정상적으로 실행될 때 호출됨
                    kakaoMap.apply{
                        setGestureEnable(GestureType.Zoom, false)
                        setGestureEnable(GestureType.Pan, false)
                        setGestureEnable(GestureType.OneFingerDoubleTap, false)
                    } // 줌 안되게 설정
                    setMarker(kakaoMap, item)
                    setInfoBalloon(kakaoMap, item)
                }

                override fun getPosition(): LatLng {
                    return LatLng.from(item.dermatologyLat, item.dermatologyLng)
                }
            })
    }

    /**
     * 병원 위치에 마커 추가
     */
    private fun setMarker(kakaoMap: KakaoMap, item: DermatologyData) {
        val pos = LatLng.from(item.dermatologyLat, item.dermatologyLng)
        val labelLayer = kakaoMap.labelManager?.layer
        val labelStyles = LabelStyles.from(LabelStyle.from(R.drawable.ic_location_pin).setIconTransition(LabelTransition.from(Transition.None, Transition.None)))
        val styles = kakaoMap.labelManager?.addLabelStyles(labelStyles)
        labelLayer?.addLabel(LabelOptions.from(LABEL_ID, pos).setStyles(styles))
    }

    /**
     * 병원 위치에 레이블 추가
     */
    private fun setInfoBalloon(kakaoMap: KakaoMap, item: DermatologyData) {
        // InfoWindow 사용 시
        val body = GuiLayout(Orientation.Horizontal)
        body.setPadding(20, 20, 20, 18)

        val image = GuiImage(R.drawable.window_body, true)
        image.setFixedArea(7,7,7,7)
        body.setBackground(image)

        val text = GuiText(item.dermatologyTitle)
        text.setTextSize(24)
        body.addView(text)

        val infoWidgetOptions = InfoWindowOptions.from(INFO_WINDOW_ID, LatLng.from(item.dermatologyLat, item.dermatologyLng)).apply {
            setBody(body)
            setBodyOffset(0f, -4f)
            setTail(GuiImage(R.drawable.window_tail, false))
            isVisible = false
        }
        kakaoMap.mapWidgetManager?.infoWindowLayer?.addInfoWindow(infoWidgetOptions)?.show()
    }
}