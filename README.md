# TagFlowView
# 浮动标签view可选择多选单选

#### 集成步骤

* 如果你的项目 Gradle 配置是在 `7.0 以下`，需要在 `build.gradle` 文件中加入

```groovy
allprojects {
    repositories {
        // JitPack 远程仓库：https://jitpack.io
        maven { url 'https://jitpack.io' }
    }
}
```

* 如果你的 Gradle 配置是 `7.0 及以上`，则需要在 `settings.gradle` 文件中加入

```groovy
dependencyResolutionManagement {
    repositories {
        // JitPack 远程仓库：https://jitpack.io
        maven { url 'https://jitpack.io' }
    }
}
```

* 配置完远程仓库后，在项目 app 模块下的 `build.gradle` 文件中加入远程依赖

```groovy
dependencies {
    api 'com.github.zengyimou:TagFlowView:0.0.9'
}
```

#使用
```
class MainActivity : ComponentActivity() {
	private lateinit var tfl: TagFlowLayout

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		tfl = findViewById(R.id.tfl)
		initTagView()
	}

	private fun getList(): List<TagSelectItem>{
		val list = mutableListOf(
			TagSelectItem().apply {
				key = "1"
				value = "test1"
			},
			TagSelectItem().apply {
				key = "2"
				value = "test2"
			},
			TagSelectItem().apply {
				key = "3"
				value = "test3"
			},
		)
		return list
	}

	private fun initTagView() {
		setAdapter(tfl = tfl, list = getList())
		setListener()
	}

	private fun setAdapter(tfl: TagFlowLayout, list: List<TagSelectItem>) {
		tfl.setAdapter(object : TagAdapter<TagSelectItem>(list) {
			override fun getView(
				parent: FlowLayout?,
				position: Int,
				t: TagSelectItem?
			): View {
				return createNewView(parent, t?.value ?: "")
			}
		})
	}

	private fun setListener(){
		tfl.setOnSelectListener(object: TagFlowLayout.OnSelectListener{
			override fun onSelected(selectPosSet: Set<Int>?) {
			}

		})
		tfl.setOnTagClickListener(object : TagFlowLayout.OnTagClickListener {
			override fun onTagClick(view: View?, position: Int, parent: FlowLayout?): Boolean {
				return false
			}

		})
	}

	/**
	 * 初始化标签
	 * @param parent FlowLayout?
	 * @param text String
	 * @return View
	 */
	fun createNewView(parent: FlowLayout?, text: String): View {
		val tv: TextView = LayoutInflater.from(this).inflate(
			R.layout.item_tag,
			parent, false
		) as TextView
		tv.text = text
		return tv
	}
}
```
