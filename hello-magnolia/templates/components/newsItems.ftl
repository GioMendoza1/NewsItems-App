<#--  Place your DOM here  -->
<div id="news-items">
    <div id="app">
        <!---Navbar Start-->
        <nav class="navbar" role="navigation" aria-label="main navigation">
            <div id="navbarBasicExample" class="navbar-menu">
                <div class="navbar-end">
                    <input class="input" type="text" v-model="currentTextFilter" placeholder="Filter By Title/Excerpt">
                    <div class="navbar-item has-dropdown is-hoverable">
                        <a class="navbar-link">
                            {{ currentFilter }}
                        </a>
                        <div class="navbar-dropdown">
                            <a class="navbar-item" @click="filterChange('Tech')">
                                Tech
                            </a>
                            <a class="navbar-item" @click="filterChange('Sports')">
                                Sports
                            </a>
                            <a class="navbar-item" @click="filterChange('Business')">
                                Business
                            </a>
                            <a class="navbar-item" @click="filterChange('Science')">
                                Science
                            </a>
                            <hr class="navbar-divider">
                            <a class="navbar-item" @click="filterChange('Category')">
                                Reset
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </nav>
        <!--Article Start-->
        <div class="grid-container">
            <div v-for="newsItem in filteredNewsItems">
                <a :href="newsItem.pageUrl" target="_blank">
                    <div class="card">
                        <div class="card-image">
                            <figure class="image is-4by3">
                                <img :src="newsItem.imgUrl" alt="Placeholder image">
                            </figure>
                        </div>
                        <news-items-comp
                            :title="newsItem.title"
                            :excerpt="newsItem.excerpt"
                            :category="newsItem.category"
                        ></news-items-comp>
                    </div>
                </a>
            </div>    
        </div>
    </div>
</div>

<#-- Place your custom scripts here  -->
<script>
    Vue.component('news-items-comp', {
        props: ['title','excerpt','category'],
        template: '\
            <div class="content-wrapper">\
                <div class="card-content">\
                    <div class="media-content">\
                        <p class="title is-4">{{ title }}</p>\
                        <p class="subtitle is-6">{{ excerpt }}</p>\
                    </div>\
                </div>\
                <footer class="card-footer">\
                    <p class="card-footer-item"> {{ category }} </p>\
                </footer>\
            </div>\
        '
    })
    new Vue({
        el: "#app",
        data: {
          newsItems: [],
          currentFilter: "Category",
          currentTextFilter: ""
        },
        computed: {
            filteredNewsItems: function() {
                let newsItems = this.newsItems
                if (this.currentTextFilter) {
                     newsItems = newsItems.filter((newsItem) => {
                        return newsItem.title.indexOf(this.currentTextFilter) !== -1 || newsItem.excerpt.indexOf(this.currentTextFilter) !== -1
                    })
                }
                if (this.currentFilter === "Tech") {
                    newsItems = newsItems.filter((newsItem) => {
                        return newsItem.category.indexOf("Tech") !== -1
                    })
                }
                else if (this.currentFilter === "Sports") {
                    newsItems = newsItems.filter((newsItem) => {
                        return newsItem.category.indexOf("Sports") !== -1
                    })
                }
                else if (this.currentFilter === "Business") {
                    newsItems = newsItems.filter((newsItem) => {
                        return newsItem.category.indexOf("Business") !== -1
                    })
                }
                else if (this.currentFilter === "Science") {
                    newsItems = newsItems.filter((newsItem) => {
                        return newsItem.category.indexOf("Science") !== -1
                    })
                }
                return newsItems
            }
        },
        methods: {
            getNewsItems: function() {
                axios.get('http://localhost:8082/JavaServlet_war_exploded/MainServlet')
                    .then(response => {
                        this.newsItems = response.data
                        
                    })
                    .catch(e => {
                        this.errors.push(e)
                    })
            },
            filterChange: function(newFilter) {
                this.currentFilter = newFilter
            }
        },
        beforeMount() {
            this.$nextTick(function () {
                this.getNewsItems()
            })
        }
      });
</script>