/**
 * Performs a deep merge of objects and returns new object. Does not modify
 * objects (immutable) and merges arrays via concatenation.
 *
 * @param {...object} objects - Objects to merge
 * @returns {object} New object with merged key/values
 */
function mergeDeep(...objects) {
    const isObject = obj => obj && typeof obj === 'object';

    return objects.reduce((prev, obj) => {
        Object.keys(obj).forEach(key => {
            const pVal = prev[key];
            const oVal = obj[key];

            if (Array.isArray(pVal) && Array.isArray(oVal)) {
                prev[key] = pVal.concat(...oVal);
            }
            else if (isObject(pVal) && isObject(oVal)) {
                prev[key] = mergeDeep(pVal, oVal);
            }
            else {
                prev[key] = oVal;
            }
        });

        return prev;
    }, {});
}
let chart_defaults = {
    maintainAspectRatio: false,
    plugins: {
        legend: {
            display: false
        },
        tooltip: {
            backgroundColor: "#f5f5f5",
            titleColor: "#333",
            bodyColor: "#666",
            bodySpacing: 4,
            xPadding: 12,
            mode: "nearest",
            intersect: 0,
            position: "nearest"
        },
    },
    responsive: true,
    scales: {
        y: {
            barPercentage: 1.6,
            grid: {
                color: "rgba(29,140,248,0.0)",
                zeroLineColor: "transparent"
            },
            ticks: {
                padding: 20,
                color: "#9a9a9a"
            }
        },
        x: {
            grid: {
                drawBorder: false,
                color: "rgba(29,140,248,0.1)",
            },
            ticks: {
                padding: 20,
                color: "#9a9a9a"
            }
        }
    }
};

let chart_with_currency_legend = {
    scales: {
        y: {
            ticks: {
                callback: function (value) {
                    return new Intl.NumberFormat('nl-NL', {style: 'currency', currency: 'EUR', maximumFractionDigits: 0}).format(value / 100);
                }
            }
        }
    }
};

let transactionsChart = {
    defaultChartOptions: chart_defaults,
    currencyChartOptions: mergeDeep(chart_defaults, chart_with_currency_legend),
};

export {
    transactionsChart,
};
