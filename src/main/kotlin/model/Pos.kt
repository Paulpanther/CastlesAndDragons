package model

data class Pos(var x: Int, var y: Int) {

    operator fun plus(pos: Pos) = Pos(this.x + pos.x, this.y + pos.y)
    operator fun minus(pos: Pos) = Pos(this.x - pos.x, this.y - pos.y)

    operator fun plus(n: Int) = Pos(this.x + n, this.y + n)
    operator fun minus(n: Int) = Pos(this.x - n, this.y - n)
    operator fun times(n: Int) = Pos(this.x * n, this.y * n)
    operator fun div(n: Int) = Pos(this.x / n, this.y / n)

    override fun toString() = "($x, $y)"
}
