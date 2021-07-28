using GaussianRandomFields;
using Plots;
using Plots.PlotMeasures;
using Colors;
using Netpbm;
using FileIO;

width = 1024
height = 1024
type = "CirculantEmbedding"

function generaterandomfield(type)
    if type == "Cholesky"
        cov = CovarianceFunction(2, Matern(1 / 2, 5 / 4))
        pts = range(0, stop = 1, length = 101)
        grf = GaussianRandomField(cov, Cholesky(), pts, pts)
    elseif type == "Spectral"
        cov = CovarianceFunction(2, Linear(1))
        pts = range(0, stop = 1, length = 51)
        grf = GaussianRandomField(cov, Spectral(), pts, pts)
    elseif type == "KarhunenLoeve"
        cov = CovarianceFunction(2, Whittle(.1))
        pts = range(0, stop = 1, length = 201)
        grf = GaussianRandomField(cov, KarhunenLoeve(500), pts, pts)
    elseif type == "CirculantEmbedding"
        cov = CovarianceFunction(2, Exponential(.5))
        pts = range(0, stop = 1, length = 1001)
        grf = GaussianRandomField(cov, CirculantEmbedding(), pts, pts, minpadding = 2001)
    end
end

function normalize(a)
    (minval, maxval) = extrema(a)
    valrange = maxval - minval

    function normalize0(v)
        (v - minval) / valrange
    end

    normalize0.(a)
end

println("Generating random field...")
@time grf = generaterandomfield(type)

println("Saving random field...")
data = sample(grf)

plot_options = (legend = false, c = :grays, framestyle = :none, size = (width,height), margin = -2mm, bottom_margin = -10mm, left_margin = -13mm)
heatmap_plot = heatmap(data; plot_options...)
contour_plot = contourf(data; plot_options...)
contour8_plot = contourf(data; levels = 8, plot_options...)
png(heatmap_plot, "heatmap.png")
png(contour_plot, "contour.png")
png(contour8_plot, "contour8.png")

normalized_data = reverse(normalize(data), dims = 1)
pgm_image_data = convert(Array{Gray}, normalized_data)
pgm_file = File(format"PGMBinary", "heatmap.pgm")
Netpbm.save(pgm_file, pgm_image_data)

println("Done")
