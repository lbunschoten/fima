<template>
    <div class="row">
        <div class="col-md-12">
            <div class="card">
                <paper-table :title="transactions.title" :sub-title="transactions.subTitle" :data="transactions.data"
                             :columns="transactions.columns">

                </paper-table>
            </div>
        </div>
    </div>
</template>
<script>
    import PaperTable from 'components/UIComponents/PaperTable.vue';

    // const tableColumns = ['Id', 'Name', 'Salary', 'Country', 'City'];
    const tableColumns = ['Id'];
    const tableData = [
        {
            id: 1,
            name: 'Dakota Rice',
            salary: '$36.738',
            country: 'Niger',
            city: 'Oud-Turnhout'
        },
        {
            id: 2,
            name: 'Minerva Hooper',
            salary: '$23,789',
            country: 'Curaçao',
            city: 'Sinaai-Waas'
        },
        {
            id: 3,
            name: 'Sage Rodriguez',
            salary: '$56,142',
            country: 'Netherlands',
            city: 'Baileux'
        },
        {
            id: 4,
            name: 'Philip Chaney',
            salary: '$38,735',
            country: 'Korea, South',
            city: 'Overland Park'
        },
        {
            id: 5,
            name: 'Doris Greene',
            salary: '$63,542',
            country: 'Malawi',
            city: 'Feldkirchen in Kärnten'
        }
    ];

    export default {
        components: {
            PaperTable
        },
        mounted() {
            fetch('/api/transaction/recent?limit=5')
                .then(response => response.json())
                .then(transactions => {
                    this.transactions.data = transactions;
                });
        },
        data() {
            return {
                transactions: {
                    title: 'Transactions',
                    subTitle: 'Latest transactions',
                    columns: [...tableColumns],
                    data: [...tableData]
                }
            }
        }
    }

</script>
<style>

</style>
