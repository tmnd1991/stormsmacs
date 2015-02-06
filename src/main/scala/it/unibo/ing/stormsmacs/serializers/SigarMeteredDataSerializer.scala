package it.unibo.ing.stormsmacs.serializers

import com.esotericsoftware.kryo.io.{Output, Input}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import it.unibo.ing.sigar.restful.model.SigarMeteredData

/**
 * @author Antonio Murgia
 * @version 28/12/14.
 */
class SigarMeteredDataSerializer extends Serializer[SigarMeteredData]{
  override def write(kryo: Kryo, output: Output, t: SigarMeteredData): Unit = {
    output.writeDouble(t.cpuPercent, 1000, true)
    output.writeDouble(t.freeMemPercent, 1000, true)
    output.writeLong(t.diskReads, true)
    output.writeLong(t.diskWrites, true)
    output.writeLong(t.diskReadBytes, true)
    output.writeLong(t.diskWriteBytes, true)
    output.writeLong(t.netInBytes, true)
    output.writeLong(t.netOutBytes, true)
    output.writeLong(t.processes, true)
    output.writeDouble(t.uptime, 1000, true)
    output.writeInt(t.numberOfCores, true)
    output.writeString(t.osName)
    output.writeString(t.cpuName)
  }

  override def read(kryo: Kryo, input: Input, aClass: Class[SigarMeteredData]): SigarMeteredData = {
    SigarMeteredData(
      cpuPercent = input.readDouble(1000, true),
      freeMemPercent = input.readDouble(1000, true),
      diskReads = input.readLong(true),
      diskWrites = input.readLong(true),
      diskReadBytes = input.readLong(true),
      diskWriteBytes = input.readLong(true),
      netInBytes = input.readLong(true),
      netOutBytes = input.readLong(true),
      processes = input.readLong(true),
      uptime = input.readDouble(1000, true),
      numberOfCores = input.readInt(true),
      osName = input.readString(),
      cpuName = input.readString()
    )
  }
}