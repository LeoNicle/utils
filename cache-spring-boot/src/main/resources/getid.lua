local key = KEYS[1]
local expire_time = ARGV[1]
local id = redis.call('get',key)
if(id == false)
then
 redis.call('set',key,1)
 redis.call("EXPIRE", key, expire_time)
 return key.."0001"
else
 redis.call('INCR',key)
 return key..string.format('%04d',id + 1)
end