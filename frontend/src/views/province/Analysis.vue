<template>
  <el-card>
    <template #header>取样分析（各地市企业占比）</template>
    <div ref="chart" style="height:420px"></div>
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import http from '../../api/http'

const chart = ref(null)

onMounted(async () => {
  const { data } = await http.get('/province/summary/by-city')
  const inst = echarts.init(chart.value)
  inst.setOption({
    title: { text: '备案企业地市分布', left: 'center' },
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    series: [
      {
        type: 'pie',
        radius: '65%',
        data: data.map((d) => ({ name: d.regionCity, value: d.count }))
      }
    ]
  })
  window.addEventListener('resize', () => inst.resize())
})
</script>
