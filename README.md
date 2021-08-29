2.2	分片上传接口
2.2.1 接口地址
file/chunkUpload        POST 接口
https://blog.csdn.net/u014150463/article/details/74044467
https://cloud.tencent.com/developer/article/1541199
https://github.com/lyb-geek/springboot-learning

2.2.2入参
	fileName 待上传文件名称
	hash 待上传文件的 md5 值
	serverName 所属系统
	chunk  第几片
	chunkSize 分片大小


流程：
	1、获取封装好的实体参数
	2、调用upload 方法，使用RandomAccessFile 特性处理分片文件，使用Redis 记录
		2.1 入参中有md5，同一个文件在分片上传的多次调用中，md5 不变，所以根据该值可以存入Redis，以md5为key，创建该文件的存储路径，作为value。
		2.2 首次被调用，则创建 Redis 信息，将第一片文件通过seek() 写入到存储文件中，同时调用checkAndSetUploadProgress() ，在存储路径下创建一个filename.conf 文件，用来记录文件处理进度。
		2.3 非最后一次调用前，通过拼md5 在Redis 中找到第一次创建的信息，获取到存储路径，如果找不到Redis 信息，说明有问题，接口返回失败；找到了Redis 信息，那么就说明可以继续处理，正常seek() write 分片文件，将分片融合信息记录到filename.conf 中,同时返回接口调用成功。
		2.4 最后一次调用，通过md5 拼key，找Redis 对应value，如果没有接口返回失败；如果有，正常seek() write 分片文件,将分片融合信息记录到filename.conf 中,判断是否完全OK（判断filename.conf 的每一个Byte 是否都为127），如果OK，那么将数据如：UUID、文件名称、文件存储路径、文件大小、是否分片、上传服务、md5 等等记录到数据库表中，记录完毕后，删除filename.conf 和Redis相关信息，接口返回成功并且携带UUID。

断点上传：
    前端记录上传到哪一步，继续上传。
    可提供查询接口，查询当前文件上传到哪个阶段（查看conf文件），继续上传即可。