/**
 * Performs a deep merge of objects and returns new object. Does not modify
 * objects (immutable) and merges arrays via concatenation.
 *
 * @param {...object} objects - Objects to merge
 * @returns {object} New object with merged key/values
 */
import {ChartOptions, TooltipItem} from "chart.js";

function mergeDeep(...objects: any[]) {
    const isObject = (obj: any) => obj && typeof obj === 'object';

    return objects.reduce((prev: any, obj: any) => {
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
let chart_defaults: ChartOptions<any> = {
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
            mode: "nearest",
            intersect: false,
            position: "nearest"
        },
    },
    responsive: true,
    scales: {
        y: {
            grid: {
                color: "rgba(29,140,248,0.0)",
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

// noinspection JSUnusedGlobalSymbols
let chart_with_currency_legend = {
    scales: {
        y: {
            ticks: {
                callback: function (value: number) {
                    return new Intl.NumberFormat('nl-NL', {style: 'currency', currency: 'EUR', maximumFractionDigits: 0}).format(value / 100);
                }
            }
        }
    },
    plugins: {
        tooltip: {
            callbacks: {
                label(tooltipItem: TooltipItem<"line">) {
                    return tooltipItem.dataset.label + ": " + new Intl.NumberFormat('nl-NL', {style: 'currency', currency: 'EUR', maximumFractionDigits: 0}).format(tooltipItem.parsed.y / 100);
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
