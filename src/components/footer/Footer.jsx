import "./footer.scss"

const Footer = () => {
  return (
    <div className="footer">
      <span>User</span>
      <span> © {new Date().getFullYear()} <strong>Agrovia</strong>. All rights reserved.</span>
    </div>
  )
}

export default Footer