<template>
    <div>
        <!--新增和搜索框-->
        <v-layout row wrap>
            <v-flex xs6>
                <v-btn color="success">新增品牌</v-btn>
            </v-flex>

            <v-flex xs6>
                <v-text-field
                        label="品牌搜索"
                        prepend-icon="search"
                        v-model="searchKey"
                        @keydown.enter="getBrands()"
                ></v-text-field>
            </v-flex>
        </v-layout>



        <!--列表-->
        <v-data-table
                :headers="headers"
                :items="brands"
                :pagination.sync="pagination"
                :total-items="totalBrand"
                :loading="loading"
                class="elevation-1"
        >
            <template v-slot:items="props">
                <td>{{ props.item.id }}</td>
                <td class="text-xs-right">{{ props.item.name }}</td>
                <td class="text-xs-right"><img v-bind:src="props.item.image" /></td>
                <td class="text-xs-right">{{ props.item.letter }}</td>
                <td class="text-xs-center">
                    <v-btn color="error">删除</v-btn>
                    <v-btn color="warning">修改</v-btn>
                </td>
            </template>
        </v-data-table>
    </div>
</template>

<script>
    export default {
        name: "MyBrand",
        data () {
            return {
                searchKey: "",
                totalBrand: 0,
                brands: [],
                loading: true,
                pagination: {},
                headers: [
                    {text: 'id',align: 'left',value: 'id'},
                    { text: '名称',align: 'right',sortable: false, value: 'name' },
                    { text: 'LOGO',align: 'right',sortable: false, value: 'logo' },
                    { text: '首字母',align: 'right', value: 'letter' },
                    { text: '操作',align: 'center',sortable: false, value: 'none' },
                ]
            }
        },
        mounted () {
            // 钩子函数会自动触发
            this.getBrands()
        },
        watch: {
            pagination: {
                deep: true,
                handler() {
                    this.getBrands();
                }
            }
        },
        methods: {
            getBrands () {
                // 效果
                this.loading = true;
                setTimeout(()=>{
                    // 调用后台的接口
                    this.$http.get("/item/brand/page", {
                        params: {
                            key: this.searchKey,
                            page: this.pagination.page,
                            rows: this.pagination.rowsPerPage,
                            sortBy: this.pagination.sortBy,
                            desc: this.pagination.descending
                        }
                    }).then(res=>{
                        // 结束特效
                        this.loading = false;
                        // 成功的回调
                        console.log(res)
                        // 跟数据赋值
                        this.brands = res.data.items;
                        this.totalBrand = res.data.total;
                    }).catch(err=>{
                        // 异常回调
                        console.log(err)
                        // 结束特效
                        this.loading = false;
                    })
                }, 500)// 500毫秒关闭特效
            }
        }
    }
</script>

<style scoped>

</style>