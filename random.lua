request = function()
    local num = wrk.path .. math.random(50)
    return wrk.format(wrk.method, num, wrk.headers, wrk.body)
end